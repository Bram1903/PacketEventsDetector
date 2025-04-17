package com.deathmotion.packeteventsdetector;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassScanner {
    public static List<String> getClassNames(Plugin plugin) throws IOException {
        List<String> classNames = new ArrayList<>();

        File file = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");
                    classNames.add(className);
                }
            }
        }

        return classNames;
    }
}

