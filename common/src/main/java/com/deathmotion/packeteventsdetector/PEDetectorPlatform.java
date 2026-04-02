package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import com.deathmotion.packeteventsdetector.scanner.ScanManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.*;
import java.util.stream.Collectors;

@Getter
public abstract class PEDetectorPlatform {

    @Getter
    private static PEDetectorPlatform instance;

    @Getter
    private static Logger logger;

    @Getter
    @Setter
    public boolean standAlone = false;

    private ScanManager scanManager;

    /**
     * Sets up the custom logger to avoid extra metadata in log output.
     */
    private static void setupLogger() {
        logger = Logger.getLogger("PacketEventsDetector");
        logger.setUseParentHandlers(false);

        for (Handler existingHandler : logger.getHandlers()) {
            logger.removeHandler(existingHandler);
            existingHandler.close();
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return "[PacketEventsDetector] " + record.getMessage() + System.lineSeparator();
            }
        });

        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    /**
     * Called when the platform is enabled.
     */
    public void commonOnEnable() {
        instance = this;
        setupLogger();

        scanManager = new ScanManager(this);

        CompletableFuture<?> scanFuture = scanManager.startScan();
        if (shouldWaitForScanCompletion()) {
            waitForScanCompletion(scanFuture);
        }
    }

    /**
     * Called when the platform gets disabled.
     */
    public void commonOnDisable() {
        if (scanManager != null) {
            scanManager.shutdown();
        }
    }

    protected boolean shouldWaitForScanCompletion() {
        return true;
    }

    public boolean shouldUseStaticDetection() {
        return standAlone;
    }

    private void waitForScanCompletion(CompletableFuture<?> scanFuture) {
        try {
            scanFuture.join();
        } catch (CancellationException ignored) {
            // Shutdown cancelled the scan.
        } catch (CompletionException ignored) {
            // ScanManager already logged the failure.
        }
    }

    protected ScannableFile createScannableFile(String name, Object sourceInstance) {
        if (sourceInstance == null) {
            logger.warning("Skipping plugin " + name + " because no plugin instance is available.");
            return null;
        }

        Class<?> sourceClass = sourceInstance.getClass();
        CodeSource codeSource = sourceClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            logger.warning("Skipping plugin " + name + " because its code source is unavailable.");
            return null;
        }

        try {
            File file = new File(codeSource.getLocation().toURI());
            return createScannableFile(name, sourceClass.getClassLoader(), file);
        } catch (URISyntaxException exception) {
            logger.warning("Skipping plugin " + name + " because its file path could not be resolved.");
            return null;
        }
    }

    protected ScannableFile createScannableFile(String name, ClassLoader classLoader, File file) {
        if (!file.isFile() || !file.getName().endsWith(".jar")) {
            return null;
        }

        return new ScannableFile(name, classLoader, file);
    }

    protected final ScannableSource scannableSource(String name, Object sourceInstance) {
        return new ScannableSource(name, sourceInstance);
    }

    protected final CompletableFuture<List<ScannableFile>> createScannableFilesAsync(List<ScannableSource> sources) {
        if (sources.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<CompletableFuture<ScannableFile>> fileTasks = sources.stream()
                .map(source -> CompletableFuture.supplyAsync(
                        () -> createScannableFile(source.getName(), source.getSourceInstance())
                ))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(fileTasks.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> fileTasks.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
    }

    public abstract CompletableFuture<List<ScannableFile>> getFiles();

    protected static final class ScannableSource {
        private final String name;
        private final Object sourceInstance;

        private ScannableSource(String name, Object sourceInstance) {
            this.name = name;
            this.sourceInstance = sourceInstance;
        }

        private String getName() {
            return name;
        }

        private Object getSourceInstance() {
            return sourceInstance;
        }
    }
}
