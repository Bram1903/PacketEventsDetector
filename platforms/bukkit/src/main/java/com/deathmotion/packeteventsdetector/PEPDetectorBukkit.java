package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.commands.PEDetectorCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PEPDetectorBukkit extends JavaPlugin {
    private final BukkitPEDetector ahi = new BukkitPEDetector(this);

    @Override
    public void onEnable() {
        ahi.commonOnEnable();
        getCommand("packeteventsdetector").setExecutor(new PEDetectorCommand<>(ahi));
    }

    @Override
    public void onDisable() {
        ahi.commonOnDisable();
    }
}