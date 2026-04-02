package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FabricPEDetector extends PEDetectorPlatform {
    @Override
    protected boolean shouldWaitForScanCompletion() {
        return false;
    }

    @Override
    public boolean shouldUseStaticDetection() {
        return true;
    }

    @Override
    public CompletableFuture<List<ScannableFile>> getFiles() {
        ClassLoader classLoader = getClass().getClassLoader();
        Map<String, FabricModFile> modFiles = new LinkedHashMap<>();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (mod.getOrigin().getKind() != ModOrigin.Kind.PATH) {
                continue;
            }

            String name = mod.getMetadata().getName();
            if (name == null || name.isEmpty()) {
                name = mod.getMetadata().getId();
            }

            for (Path path : mod.getOrigin().getPaths()) {
                File file = path.toFile();
                String key = file.getAbsolutePath();
                modFiles.putIfAbsent(key, new FabricModFile(name, file));
            }
        }

        if (modFiles.isEmpty()) {
            return CompletableFuture.completedFuture(new ArrayList<ScannableFile>());
        }

        List<FabricModFile> filesToScan = new ArrayList<>(modFiles.values());
        return CompletableFuture.supplyAsync(() -> filesToScan.stream()
                .map(file -> createScannableFile(file.getName(), classLoader, file.getFile()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    private static final class FabricModFile {
        private final String name;
        private final File file;

        private FabricModFile(String name, File file) {
            this.name = name;
            this.file = file;
        }

        private String getName() {
            return name;
        }

        private File getFile() {
            return file;
        }
    }
}
