package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BungeePEDetector extends PEDetectorPlatform {
    private final Plugin plugin;

    public BungeePEDetector(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public CompletableFuture<List<ScannableFile>> getFiles() {
        List<ScannableSource> sources = new ArrayList<>();

        for (Plugin installedPlugin : plugin.getProxy().getPluginManager().getPlugins()) {
            sources.add(scannableSource(installedPlugin.getDescription().getName(), installedPlugin));
        }

        return createScannableFilesAsync(sources);
    }
}
