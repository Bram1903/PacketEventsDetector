package com.deathmotion.packeteventsdetector.commands;

import com.deathmotion.packeteventsdetector.PEDetectorPlatform;
import com.deathmotion.packeteventsdetector.models.PEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PEDetectorCommand<P> implements CommandExecutor {

    private final PEDetectorPlatform<P> platform;

    public PEDetectorCommand(PEDetectorPlatform<P> platform) {
        this.platform = platform;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("PacketEventsDetector.Command")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return false;
        }

        List<PEPlugin> plugins = platform.getScanManager().getPlugins();
        if (plugins.isEmpty()) {
            sender.sendMessage("§cNo plugins using PacketEvents were found.");
            return false;
        }

        sender.sendMessage("§aPlugins using PacketEvents:");
        for (PEPlugin plugin : plugins) {
            sender.sendMessage("§7- §6" + plugin.getName() + "§7 (PE: " + plugin.getVersion() + ")");
        }

        return true;
    }
}
