package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.models.PacketEventsPlugin;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class PacketEventsDetector extends JavaPlugin {

    private final List<PacketEventsPlugin> plugins = new ArrayList<>();

    @Getter
    private static PacketEventsDetector instance;

    @Override
    public void onEnable() {
        instance = this;

        for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
            try {
                List<String> classNames = PluginClassScanner.getClassNames(plugin);
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

                for (String className : classNames) {
                    if (!className.endsWith("PacketEvents")) continue;

                    try {
                        Class<?> packetEventsClass = Class.forName(className, false, pluginClassLoader);
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

                        plugins.add(new PacketEventsPlugin(plugin.getName(), version));
                        break;
                    } catch (Throwable ignore) {
                        // Ignore the exception and continue scanning
                    }
                }
            } catch (Exception e) {
                getLogger().warning("Failed to scan plugin " + plugin.getName() + ": " + e.getMessage());
            }
        }

        if (plugins.isEmpty()) {
            getLogger().warning("No PacketEvents plugins detected.");
        } else {
            for (PacketEventsPlugin plugin : plugins) {
                getLogger().info("Detected PacketEvents plugin: " + plugin.name() + " - Version: " + plugin.version());
            }
        }
    }


    @Override
    public void onDisable() {

    }
}
