package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class BukkitPEDetector extends PEDetectorPlatform {

    private final JavaPlugin plugin;

    public BukkitPEDetector(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public CompletableFuture<List<ScannableFile>> getFiles() {
        List<ScannableSource> sources = new ArrayList<>();

        for (Plugin installedPlugin : plugin.getServer().getPluginManager().getPlugins()) {
            sources.add(scannableSource(installedPlugin.getName(), installedPlugin));
        }

        return createScannableFilesAsync(sources);
    }
}
