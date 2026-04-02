package com.deathmotion.packeteventsdetector.scanner;

import com.deathmotion.packeteventsdetector.models.ScannableFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassScanner {
    private static final String CLASS_EXTENSION = ".class";
    private static final String PACKET_EVENTS_CLASS_SUFFIX = "PacketEvents" + CLASS_EXTENSION;

    private ClassScanner() {
    }

    public static List<String> findPacketEventsClasses(ScannableFile scannableFile) throws IOException {
        List<String> cachedClassNames = scannableFile.getClassNames();
        if (cachedClassNames != null) {
            return cachedClassNames;
        }

        List<String> classNames = new ArrayList<>();
        File file = scannableFile.getFile();

        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(PACKET_EVENTS_CLASS_SUFFIX)) {
                    continue;
                }

                String className = entry.getName()
                        .replace('/', '.')
                        .replace(CLASS_EXTENSION, "");
                classNames.add(className);
            }
        }

        List<String> discoveredClasses = Collections.unmodifiableList(classNames);
        scannableFile.setClassNames(discoveredClasses);
        return discoveredClasses;
    }
}
