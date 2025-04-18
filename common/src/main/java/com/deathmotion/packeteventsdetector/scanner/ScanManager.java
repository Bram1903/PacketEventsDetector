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
                PEPlugin plugin;
                if (platform.isStandAlone()) {
                    plugin = detectPluginStatic(file);
                } else {
                    plugin = detectPlugin(file);
                }

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

    private PEPlugin detectPlugin(ScannableFile scannableFile) throws IOException {
        for (String className : ClassScanner.getClassNames(scannableFile.getFile())) {
            if (!className.endsWith("PacketEvents")) continue;

            try {
                Class<?> clazz = scannableFile.getClassLoader().loadClass(className);
                Method getApiMethod = clazz.getDeclaredMethod("getAPI");
                Object apiInstance = getApiMethod.invoke(null);

                if (apiInstance != null) {
                    String version = getVersionSafe(apiInstance);
                    return new PEPlugin(scannableFile.getName(), version);
                }

            } catch (NoSuchMethodException ignored) {
                // Class doesn't have a `getAPI` method
            } catch (Throwable ignored) {
                // Ignore other exceptions during scanning
            }
        }
        return null;
    }

    private PEPlugin detectPluginStatic(ScannableFile scannableFile) {
        try {
            for (String className : ClassScanner.getClassNames(scannableFile.getFile())) {
                if (className.endsWith("PacketEvents")) {
                    return new PEPlugin(scannableFile.getName(), "Unknown (Static Detection)");
                }
            }
        } catch (IOException e) {
            PEDetectorPlatform.getLogger().log(SEVERE, "Failed to scan file: " + scannableFile.getName() + " for class names", e);
        }
        return null;
    }

    private String getVersionSafe(Object apiInstance) {
        try {
            Method getVersionMethod = apiInstance.getClass().getMethod("getVersion");
            Object versionObj = getVersionMethod.invoke(apiInstance);
            return versionObj != null ? versionObj.toString() : "Unknown";
        } catch (Throwable ignored) {
            return "Unknown";
        }
    }

}
