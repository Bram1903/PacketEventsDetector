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
import com.google.inject.Inject;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;

import java.nio.file.Path;
import java.util.List;

public class SpongePEDetector extends PEDetectorPlatform<Platform> {

    private final Path configDirectory;

    @Inject
    public SpongePEDetector(@ConfigDir(sharedRoot = false) Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    @Override
    public Platform getPlatform() {
        return Sponge.platform();
    }

    @Override
    public List<ScannableFile> getFiles() {
        return List.of();
    }
}
