package com.bandosfah;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/** Accessed only on the plugin's audio worker, never the game thread. */
final class FahSoundPlayer implements AutoCloseable
{
    private Clip clip;

    void load(String path) throws Exception
    {
        close();
        try (InputStream source = path.trim().isEmpty()
                ? FahSoundPlayer.class.getResourceAsStream("fah.wav")
                : new FileInputStream(new File(path));
             BufferedInputStream buffered = new BufferedInputStream(source);
             AudioInputStream stream = AudioSystem.getAudioInputStream(buffered))
        {
            Clip next = AudioSystem.getClip();
            try { next.open(stream); }
            catch (Exception e) { next.close(); throw e; }
            clip = next;
        }
    }

    void play(int volume)
    {
        if (clip == null || volume <= 0) { return; }
        clip.stop();
        clip.setFramePosition(0);
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = (float) (20 * Math.log10(Math.min(100, volume) / 100.0));
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        }
        clip.start();
    }

    @Override
    public void close()
    {
        if (clip != null) { clip.stop(); clip.close(); clip = null; }
    }
}
