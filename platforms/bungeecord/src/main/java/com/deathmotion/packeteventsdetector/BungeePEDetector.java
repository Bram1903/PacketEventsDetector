package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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
    public List<ScannableFile> getFiles() {
        List<ScannableFile> files = new ArrayList<>();

        for (Plugin installedPlugin : plugin.getProxy().getPluginManager().getPlugins()) {
            addScannableFile(files, installedPlugin.getDescription().getName(), installedPlugin);
        }

        return files;
    }
}
