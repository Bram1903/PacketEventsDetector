package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VelocityPEDetector extends PEDetectorPlatform {
    private final ProxyServer server;

    public VelocityPEDetector(ProxyServer server) {
        this.server = server;
    }

    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public CompletableFuture<List<ScannableFile>> getFiles() {
        List<ScannableSource> sources = new ArrayList<>();

        for (PluginContainer plugin : server.getPluginManager().getPlugins()) {
            Object pluginInstance = plugin.getInstance().orElse(null);
            String name = plugin.getDescription().getName().orElse(plugin.getDescription().getId());
            sources.add(scannableSource(name, pluginInstance));
        }

        return createScannableFilesAsync(sources);
    }
}
