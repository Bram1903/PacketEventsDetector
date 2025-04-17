package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import com.deathmotion.packeteventsdetector.scanner.ScanManager;
import lombok.Getter;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;

@Getter
public abstract class PEDetectorPlatform<P> {

    @Getter
    private static PEDetectorPlatform<?> instance;

    @Getter
    private static Logger logger;

    private ScanManager<P> ScanManager;

    /**
     * Called when the platform is enabled.
     */
    public void commonOnEnable() {
        instance = this;
        setupLogger();

        ScanManager = new ScanManager<>(this);
    }

    /**
     * Called when the platform gets disabled.
     */
    public void commonOnDisable() {
    }

    /**
     * Sets up the custom logger to avoid extra metadata in log output.
     */
    private static void setupLogger() {
        logger = Logger.getLogger("PacketEventsDetector");
        logger.setUseParentHandlers(false);

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
     * Gets the platform.
     *
     * @return The platform.
     */
    public abstract P getPlatform();


    public abstract List<ScannableFile> getFiles();
}
