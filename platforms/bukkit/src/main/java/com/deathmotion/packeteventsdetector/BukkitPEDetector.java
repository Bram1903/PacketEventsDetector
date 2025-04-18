package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BukkitPEDetector extends PEDetectorPlatform<JavaPlugin> {

    private final JavaPlugin plugin;

    public BukkitPEDetector(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public JavaPlugin getPlatform() {
        return this.plugin;
    }

    @Override
    public List<ScannableFile> getFiles() {
        List<ScannableFile> files = new ArrayList<>();

        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins()) {
            File file = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            files.add(new ScannableFile(plugin.getName(), plugin.getClass().getClassLoader(), file));
        }

        return files;
    }
}