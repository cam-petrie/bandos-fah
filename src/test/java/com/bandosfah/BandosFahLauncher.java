package com.bandosfah;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public final class BandosFahLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(BandosFahPlugin.class);
        RuneLite.main(args);
    }
}
