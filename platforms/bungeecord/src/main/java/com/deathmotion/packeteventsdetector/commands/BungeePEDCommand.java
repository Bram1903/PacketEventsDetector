/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.packeteventsdetector.commands;

import com.deathmotion.packeteventsdetector.PEDetectorBungee;
import com.sun.tools.javac.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeePEDCommand extends Command implements TabExecutor {

    private final PEDetectorBungee plugin;

    public BungeePEDCommand(PEDetectorBungee plugin) {
        super("AntiHealthIndicator", null, "ahi");
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return List.of();
    }
}

