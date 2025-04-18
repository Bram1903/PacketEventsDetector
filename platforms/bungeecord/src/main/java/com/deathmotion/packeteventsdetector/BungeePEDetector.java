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

import com.deathmotion.packeteventsdetector.models.ScannableFile;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

@Getter
public class BungeePEDetector extends PEDetectorPlatform<Plugin> {

    private final Plugin plugin;

    public BungeePEDetector(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlatform() {
        return this.plugin;
    }

    @Override
    public List<ScannableFile> getFiles() {
        return List.of();
    }
}
