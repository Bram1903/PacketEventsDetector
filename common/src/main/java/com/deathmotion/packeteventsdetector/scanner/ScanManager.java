package com.deathmotion.packeteventsdetector.scanner;

import com.deathmotion.packeteventsdetector.PEDetectorPlatform;
import com.deathmotion.packeteventsdetector.models.PEPlugin;
import com.deathmotion.packeteventsdetector.models.ScannableFile;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.logging.Level.SEVERE;

public class ScanManager<P> {

    private final PEDetectorPlatform<P> platform;

    @Getter
    private final List<PEPlugin> plugins;

    public ScanManager(PEDetectorPlatform<P> platform) {
        this.platform = platform;
        this.plugins = new ArrayList<>();

        startScan();
    }

    private void startScan() {
        PEDetectorPlatform.getLogger().info("Starting scan...");
        List<ScannableFile> scannableFiles = platform.getFiles();

        PEDetectorPlatform.getLogger().info("Detected " + scannableFiles.size() + " files.");

        scannableFiles.parallelStream().forEach(file -> {
            try {
                PEPlugin plugin =  detectPlugin(file);
                if (plugin == null) return;

                plugins.add(plugin);
                PEDetectorPlatform.getLogger().info("Detected plugin: " + plugin.getName() + " (PE: " + plugin.getVersion() + ")");
            } catch (Exception e) {
                PEDetectorPlatform.getLogger().log(SEVERE, "Failed to scan file: " + file.getName(), e);
            }
        });

        if (plugins.isEmpty()) {
            PEDetectorPlatform.getLogger().info("No plugins using PacketEvents detected");
        } else {
            PEDetectorPlatform.getLogger().info("Scan completed. Detected " + plugins.size() + " plugin(s) using PacketEvents.");
        }
    }

    public PEPlugin detectPlugin(ScannableFile scannableFile) throws IOException {
       List<String> classNames = ClassScanner.getClassNames(scannableFile.getFile());
       PEPlugin plugin = null;

        for (String className : classNames) {
            if (!className.endsWith("PacketEvents")) continue;

            try {
                Class<?> packetEventsClass = Class.forName(className, false, scannableFile.getClassLoader());
                Method getApiMethod = packetEventsClass.getDeclaredMethod("getAPI");

                Object apiInstance = getApiMethod.invoke(null); // static method
                if (apiInstance == null) continue;

                Method getVersionMethod = apiInstance.getClass().getMethod("getVersion");
                Object peVersion = getVersionMethod.invoke(apiInstance);

                String version;
                try {
                    Method toStringMethod = peVersion.getClass().getMethod("toString");
                    version = (String) toStringMethod.invoke(peVersion);
                } catch (Throwable ignored) {
                    version = "Unknown";
                }

                plugin = new PEPlugin(scannableFile.getName(), version);
                break;
            } catch (Throwable ignore) {
                // Ignore the exception and continue scanning
            }
        }

        return plugin;
    }
}
