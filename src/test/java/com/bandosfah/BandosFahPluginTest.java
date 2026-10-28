package com.bandosfah;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class BandosFahPluginTest
{
    private BandosFahPlugin plugin;
    private Client client;
    private NPC graardor;

    private static AnimationChanged animationChanged(Actor actor)
    {
        AnimationChanged event = new AnimationChanged();
        event.setActor(actor);
        return event;
    }

    @Before
    public void setUp()
    {
        plugin = spy(new BandosFahPlugin());
        client = mock(Client.class);
        plugin.client = client;
        doNothing().when(plugin).playSound();
        graardor = mock(NPC.class);
        when(graardor.getId()).thenReturn(2215);
        when(graardor.getAnimation()).thenReturn(7021);
        when(client.getTickCount()).thenReturn(100);
    }

    @Test
    public void slamPlaysOnceDespiteDuplicateEvents()
    {
        plugin.onAnimationChanged(animationChanged(graardor));
        plugin.onAnimationChanged(animationChanged(graardor));
        verify(plugin, times(1)).playSound();
        when(client.getTickCount()).thenReturn(106);
        plugin.onAnimationChanged(animationChanged(graardor));
        verify(plugin, times(2)).playSound();
    }

    @Test
    public void meleeDefenceAndDeathAreSilent()
    {
        for (int animation : new int[]{7018, 7019, 7020, -1})
        {
            when(graardor.getAnimation()).thenReturn(animation);
            plugin.onAnimationChanged(animationChanged(graardor));
        }
        verify(plugin, never()).playSound();
    }

    @Test
    public void otherNpcsAndPlayersAreSilent()
    {
        when(graardor.getId()).thenReturn(2216);
        plugin.onAnimationChanged(animationChanged(graardor));
        plugin.onAnimationChanged(animationChanged(mock(Player.class)));
        verify(plugin, never()).playSound();
    }
}
