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

import com.deathmotion.packeteventsdetector.PEDetectorPlugin;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

public class VelocityPEDCommand implements SimpleCommand {

    private final PEDetectorPlugin plugin;

    public VelocityPEDCommand(PEDetectorPlugin plugin, ProxyServer server) {
        this.plugin = plugin;

        CommandMeta commandMeta = server.getCommandManager().metaBuilder("packeteventsdetector")
                .aliases("ped")
                .build();
        server.getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return List.of();
    }
}
