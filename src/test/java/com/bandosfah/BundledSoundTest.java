package com.bandosfah;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.junit.Test;
import static org.junit.Assert.*;

public class BundledSoundTest
{
    @Test
    public void bundledClipIsDecodablePcmAudio() throws Exception
    {
        try (InputStream source = FahSoundPlayer.class.getResourceAsStream("fah.wav"))
        {
            assertNotNull("The default sound must be packaged with the plugin", source);
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(source)))
            {
                assertEquals(AudioFormat.Encoding.PCM_SIGNED, audio.getFormat().getEncoding());
                assertEquals(16, audio.getFormat().getSampleSizeInBits());
                double seconds = audio.getFrameLength() / audio.getFormat().getFrameRate();
                assertTrue("Expected a short, nonempty meme clip", seconds > 0 && seconds < 5);
                assertTrue(audio.read(new byte[4096]) > 0);
            }
        }
    }
}
