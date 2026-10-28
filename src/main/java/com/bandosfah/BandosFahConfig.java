package com.bandosfah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(BandosFahConfig.GROUP)
public interface BandosFahConfig extends Config
{
    String GROUP = "bandosfah";

    @ConfigItem(keyName = "soundPath", name = "Sound file", description = "Leave blank for the bundled FAHHHH. Or select a custom PCM WAV in the FAH sidebar.", position = 0)
    default String soundPath() { return ""; }

    @Range(min = 0, max = 100)
    @ConfigItem(keyName = "volume", name = "Volume", description = "FAHHHH volume, independent of game audio (0 mutes)", position = 1)
    default int volume() { return 70; }
}
