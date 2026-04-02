package com.deathmotion.packeteventsdetector.scanner;

import com.deathmotion.packeteventsdetector.PEDetectorPlatform;
import com.deathmotion.packeteventsdetector.models.PEPlugin;
import com.deathmotion.packeteventsdetector.models.ScannableFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;

public class ScanManager {
    private static final int MAX_SCAN_THREADS = 4;
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

    private final PEDetectorPlatform platform;
    private final ExecutorService scanExecutor;
    private volatile CompletableFuture<List<PEPlugin>> activeScan = CompletableFuture.completedFuture(Collections.emptyList());

    public ScanManager(PEDetectorPlatform platform) {
        this.platform = platform;
        this.scanExecutor = Executors.newFixedThreadPool(getWorkerCount(), createThreadFactory());
    }

    public CompletableFuture<List<PEPlugin>> startScan() {
        PEDetectorPlatform.getLogger().info("Starting scan...");
        List<ScannableFile> scannableFiles = platform.getFiles();
        PEDetectorPlatform.getLogger().info("Detected " + scannableFiles.size() + " files.");

        if (scannableFiles.isEmpty()) {
            List<PEPlugin> emptyResult = Collections.emptyList();
            logSummary(emptyResult);
            scanExecutor.shutdown();
            activeScan = CompletableFuture.completedFuture(emptyResult);
            return activeScan;
        }

        List<CompletableFuture<PEPlugin>> scanTasks = scannableFiles.stream()
                .map(this::createScanTask)
                .collect(Collectors.toList());

        CompletableFuture<List<PEPlugin>> scanFuture = CompletableFuture
                .allOf(scanTasks.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> scanTasks.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .whenComplete((plugins, throwable) -> {
                    try {
                        if (throwable == null) {
                            logSummary(plugins);
                            return;
                        }

                        Throwable cause = unwrap(throwable);
                        if (!(cause instanceof CancellationException)) {
                            PEDetectorPlatform.getLogger().log(SEVERE, "Scan failed", cause);
                        }
                    } finally {
                        scanExecutor.shutdown();
                    }
                });

        activeScan = scanFuture;
        return scanFuture;
    }

    public void shutdown() {
        activeScan.cancel(true);
        scanExecutor.shutdownNow();
    }

    private CompletableFuture<PEPlugin> createScanTask(ScannableFile file) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return scanFile(file);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                }, scanExecutor)
                .handle((plugin, throwable) -> {
                    if (throwable != null) {
                        Throwable cause = unwrap(throwable);
                        if (!(cause instanceof CancellationException)) {
                            PEDetectorPlatform.getLogger().log(SEVERE, "Failed to scan file: " + file.getName(), cause);
                        }
                        return null;
                    }

                    if (plugin != null) {
                        PEDetectorPlatform.getLogger().info(
                                "Detected plugin: " + plugin.getName() + " (PE: " + plugin.getVersion() + ")"
                        );
                    }

                    return plugin;
                });
    }

    private PEPlugin scanFile(ScannableFile scannableFile) throws IOException {
        if (platform.isStandAlone()) {
            return detectPluginStatic(scannableFile);
        }

        return detectPlugin(scannableFile);
    }

    private void logSummary(List<PEPlugin> plugins) {
        if (plugins.isEmpty()) {
            PEDetectorPlatform.getLogger().info("No plugins using PacketEvents detected");
            return;
        }

        PEDetectorPlatform.getLogger().info(
                "Scan completed. Detected " + plugins.size() + " plugin(s) using PacketEvents."
        );
    }

    private int getWorkerCount() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return Math.max(2, Math.min(MAX_SCAN_THREADS, availableProcessors));
    }

    private ThreadFactory createThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable, "pe-detector-scan-" + THREAD_COUNTER.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null
                && (current instanceof java.util.concurrent.CompletionException
                || current instanceof java.util.concurrent.ExecutionException)) {
            current = current.getCause();
        }
        return current;
    }

    private PEPlugin detectPlugin(ScannableFile scannableFile) throws IOException {
        for (String className : ClassScanner.findPacketEventsClasses(scannableFile)) {
            try {
                Class<?> clazz = scannableFile.getClassLoader().loadClass(className);
                Method getApiMethod = clazz.getDeclaredMethod("getAPI");
                Object apiInstance = getApiMethod.invoke(null);

                if (apiInstance != null) {
                    String version = getVersionSafe(apiInstance);
                    return new PEPlugin(scannableFile.getName(), version);
                }
            } catch (NoSuchMethodException ignored) {
                // Class doesn't have a `getAPI` method.
            } catch (Throwable ignored) {
                // Ignore other exceptions during scanning.
            }
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

    private PEPlugin detectPluginStatic(ScannableFile scannableFile) throws IOException {
        if (!ClassScanner.findPacketEventsClasses(scannableFile).isEmpty()) {
            return new PEPlugin(scannableFile.getName(), "Unknown (Static Detection)");
        }

        return null;
    }
}
