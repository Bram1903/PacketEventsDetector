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

        for (String className : classNames) {
            if (!className.endsWith("PacketEvents")) continue;

            try {
                Class<?> clazz = scannableFile.getClassLoader().loadClass(className);
                Method getApiMethod = clazz.getDeclaredMethod("getAPI");

                Object apiInstance = getApiMethod.invoke(null);
                if (apiInstance == null) continue;

                Method getVersionMethod = apiInstance.getClass().getMethod("getVersion");
                Object versionObj = getVersionMethod.invoke(apiInstance);
                String version = (versionObj != null) ? versionObj.toString() : "Unknown";

                return new PEPlugin(scannableFile.getName(), version);

            } catch (Throwable t) {
                PEDetectorPlatform.getLogger().fine("Failed loading " + className + ": " + t.getMessage());
            }
        }

        return null;
    }

}
