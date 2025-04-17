package com.deathmotion.packeteventsdetector.commands;

import com.deathmotion.packeteventsdetector.PEDetectorPlatform;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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

        sender.sendMessage("Detected: " + platform.getScanManager().getPlugins().size() + " plugin(s).");

        return true;
    }
}
