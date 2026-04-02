package com.deathmotion.packeteventsdetector.scanner;

import com.deathmotion.packeteventsdetector.PEDetectorPlatform;
import com.deathmotion.packeteventsdetector.models.PEPlugin;
import com.deathmotion.packeteventsdetector.models.ScannableFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;

public class ScanManager {
    private static final int MAX_SCAN_THREADS = 4;
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final PEDetectorPlatform platform;
    private final ExecutorService scanExecutor;
    private volatile CompletableFuture<List<PEPlugin>> activeScan = CompletableFuture.completedFuture(Collections.emptyList());

    public ScanManager(PEDetectorPlatform platform) {
        this.platform = platform;
        this.scanExecutor = Executors.newFixedThreadPool(getWorkerCount(), createThreadFactory());
    }

    public CompletableFuture<List<PEPlugin>> startScan() {
        PEDetectorPlatform.getLogger().info("Starting scan...");
        CompletableFuture<List<PEPlugin>> scanFuture = platform.getFiles()
                .thenCompose(this::startFileScan)
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

    private CompletableFuture<List<PEPlugin>> startFileScan(List<ScannableFile> scannableFiles) {
        if (scannableFiles.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<PEPlugin>> scanTasks = scannableFiles.stream()
                .map(this::createScanTask)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(scanTasks.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> scanTasks.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(PEPlugin::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList()));
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
                    return plugin;
                });
    }

    private PEPlugin scanFile(ScannableFile scannableFile) throws IOException {
        if (platform.shouldUseStaticDetection()) {
            return detectPluginStatic(scannableFile);
        }

        return detectPlugin(scannableFile);
    }

    private void logSummary(List<PEPlugin> plugins) {
        StringBuilder message = new StringBuilder("Scan completed.");

        if (!plugins.isEmpty()) {
            message.append(LINE_SEPARATOR)
                    .append("Detected plugins/mods using PacketEvents:");

            for (PEPlugin plugin : plugins) {
                message.append(LINE_SEPARATOR)
                        .append(" - ")
                        .append(plugin.getName())
                        .append(" (PE: ")
                        .append(plugin.getVersion())
                        .append(')');
            }
        } else {
            message.append(LINE_SEPARATOR)
                    .append("No plugins or mods using PacketEvents detected.");
        }

        message.append(LINE_SEPARATOR)
                .append("Total detected: ")
                .append(plugins.size())
                .append(" plugin/mod(s).");

        PEDetectorPlatform.getLogger().info(message.toString());
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
