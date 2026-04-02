package com.deathmotion.packeteventsdetector;

import net.md_5.bungee.api.plugin.Plugin;

public class PEPDetectorBungee extends Plugin {
    private final BungeePEDetector peDetector = new BungeePEDetector(this);

    @Override
    public void onEnable() {
        peDetector.commonOnEnable();
    }

    @Override
    public void onDisable() {
        peDetector.commonOnDisable();
    }
}
