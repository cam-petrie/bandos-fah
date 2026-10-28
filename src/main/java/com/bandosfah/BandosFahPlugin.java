package com.bandosfah;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name = "Bandos FAHHHH", description = "Plays FAHHHH when General Graardor slams the ground", tags = {"bandos", "graardor", "sound", "meme"})
public class BandosFahPlugin extends Plugin
{
    // RuneLite gameval: GODWARS_BANDOS_AVATAR and GODWARS_BANDOS_RANGED.
    static final int GRAARDOR = 2215;
    static final int GROUND_SLAM = 7021;
    private static final Logger log = LoggerFactory.getLogger(BandosFahPlugin.class);

    @Inject Client client;
    @Inject BandosFahConfig config;
    @Inject private ConfigManager configManager;
    @Inject private ClientToolbar toolbar;

    private final Map<NPC, Integer> lastSlams = new IdentityHashMap<>();
    private final FahSoundPlayer sound = new FahSoundPlayer();
    private volatile ExecutorService audioWorker;
    private JLabel status;
    private NavigationButton navigation;

    @Provides
    BandosFahConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(BandosFahConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        audioWorker = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "bandos-fah-audio");
            t.setDaemon(true);
            return t;
        });
        onEdt(() -> {
            PluginPanel panel = new PluginPanel() {};
            panel.setLayout(new GridLayout(0, 1, 0, 8));
            panel.add(new JLabel("Bandos FAHHHH"));
            JButton choose = new JButton("Choose FAHHHH WAV…");
            choose.addActionListener(e -> {
                JFileChooser picker = new JFileChooser();
                picker.setFileFilter(new FileNameExtensionFilter("WAV audio", "wav"));
                if (picker.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION)
                {
                    configManager.setConfiguration(BandosFahConfig.GROUP, "soundPath", picker.getSelectedFile().getAbsolutePath());
                }
            });
            panel.add(choose);
            JButton reset = new JButton("Use original FAHHHH");
            reset.addActionListener(e -> configManager.setConfiguration(BandosFahConfig.GROUP, "soundPath", ""));
            panel.add(reset);
            JButton preview = new JButton("Play FAHHHH");
            preview.addActionListener(e -> playSound());
            panel.add(preview);
            status = new JLabel("Loading FAHHHH…");
            panel.add(status);
            BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = icon.createGraphics();
            g.setColor(new Color(222, 180, 92));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            g.drawString("FAH", 1, 22);
            g.dispose();
            navigation = NavigationButton.builder().tooltip("Bandos FAHHHH").icon(icon).panel(panel).priority(8).build();
            toolbar.addNavigation(navigation);
        });
        reloadSound();
    }

    @Override
    protected void shutDown() throws Exception
    {
        ExecutorService worker = audioWorker;
        audioWorker = null;
        if (worker != null)
        {
            worker.execute(sound::close);
            worker.shutdown();
        }
        lastSlams.clear();
        onEdt(() -> {
            if (navigation != null) { toolbar.removeNavigation(navigation); navigation = null; }
            status = null;
        });
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if (!config.effectEnabled()) { return; }
        if (!(event.getActor() instanceof NPC)) { return; }
        NPC npc = (NPC) event.getActor();
        if (npc.getId() != GRAARDOR || npc.getAnimation() != GROUND_SLAM) { return; }
        int tick = client.getTickCount();
        Integer previous = lastSlams.put(npc, tick);
        if (previous == null || previous != tick) { playSound(); }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) { lastSlams.remove(event.getNpc()); }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN) { lastSlams.clear(); }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (BandosFahConfig.GROUP.equals(event.getGroup()) && "soundPath".equals(event.getKey())) { reloadSound(); }
    }

    private void reloadSound()
    {
        ExecutorService worker = audioWorker;
        if (worker == null) { return; }
        String path = config.soundPath();
        worker.execute(() -> {
            try { sound.load(path); setStatus("Sound ready. Try Play FAHHHH."); }
            catch (Exception e)
            {
                setStatus("Cannot load audio. Check file/output.");
                log.debug("Could not load FAHHHH sound", e);
            }
        });
    }

    void playSound()
    {
        ExecutorService worker = audioWorker;
        if (worker == null) { return; }
        int volume = config.volume();
        worker.execute(() -> {
            try { sound.play(volume); }
            catch (Exception e) { setStatus("Audio playback failed. See logs."); log.warn("FAHHHH playback failed", e); }
        });
    }

    private void setStatus(String text)
    {
        SwingUtilities.invokeLater(() -> { if (status != null) { status.setText(text); status.setToolTipText(text); } });
    }

    private static void onEdt(Runnable action) throws Exception
    {
        if (SwingUtilities.isEventDispatchThread()) { action.run(); }
        else { SwingUtilities.invokeAndWait(action); }
    }
}
