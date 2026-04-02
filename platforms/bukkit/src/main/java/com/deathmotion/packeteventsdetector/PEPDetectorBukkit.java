package com.deathmotion.packeteventsdetector;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PEPDetectorBukkit extends JavaPlugin {
    private final BukkitPEDetector peDetector = new BukkitPEDetector(this);

    @Override
    public void onEnable() {
        peDetector.commonOnEnable();
    }

    @Override
    public void onDisable() {
        peDetector.commonOnDisable();
    }
}