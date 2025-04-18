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

package com.deathmotion.packeteventsdetector;

import com.deathmotion.packeteventsdetector.commands.VelocityPEDCommand;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import java.nio.file.Path;

public class PEDetectorPlugin {
    private final ProxyServer server;

    @Getter
    private final VelocityPEDetector ped;

    @Inject
    public PEDetectorPlugin(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.ped = new VelocityPEDetector(server, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
        ped.commonOnEnable();
        registerCommands();
    }

    @Subscribe()
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
        ped.commonOnDisable();
    }

    private void registerCommands() {
        new VelocityPEDCommand(this, server);
    }
}