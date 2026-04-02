package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;

public class SpongePEDetector extends PEDetectorPlatform {
    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public List<ScannableFile> getFiles() {
        List<ScannableFile> files = new ArrayList<>();

        for (PluginContainer plugin : Sponge.pluginManager().plugins()) {
            Object pluginInstance = plugin.instance();
            String name = plugin.metadata().name().orElse(plugin.metadata().id());
            addScannableFile(files, name, pluginInstance);
        }

        return files;
    }
}
