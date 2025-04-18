package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.commands.PEDetectorCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PEDetectorBukkit extends JavaPlugin {
    private final BukkitPEDetector ped = new BukkitPEDetector(this);

    @Override
    public void onEnable() {
        ped.commonOnEnable();
        getCommand("packeteventsdetector").setExecutor(new PEDetectorCommand<>(ped));
    }

    @Override
    public void onDisable() {
        ped.commonOnDisable();
    }
}