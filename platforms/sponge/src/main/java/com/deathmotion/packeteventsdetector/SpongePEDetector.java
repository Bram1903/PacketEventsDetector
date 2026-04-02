package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SpongePEDetector extends PEDetectorPlatform {
    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public CompletableFuture<List<ScannableFile>> getFiles() {
        List<ScannableSource> sources = new ArrayList<>();

        for (PluginContainer plugin : Sponge.pluginManager().plugins()) {
            Object pluginInstance = plugin.instance();
            String name = plugin.metadata().name().orElse(plugin.metadata().id());
            sources.add(scannableSource(name, pluginInstance));
        }

        return createScannableFilesAsync(sources);
    }
}
