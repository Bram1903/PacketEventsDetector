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

import com.deathmotion.packeteventsdetector.PEDetectorSponge;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.ArgumentReader;

import java.util.List;
import java.util.Optional;

public class SpongeAHICommand implements Command.Raw {
    private final PEDetectorSponge plugin;

    public SpongeAHICommand(PEDetectorSponge plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public CommandResult process(@NotNull CommandCause cause, @NotNull ArgumentReader.Mutable arguments) {
        return CommandResult.success();
    }

    @Override
    @NotNull
    public List<CommandCompletion> complete(@NotNull CommandCause cause, @NotNull ArgumentReader.Mutable arguments) {

    }

    @Override
    public boolean canExecute(@NotNull CommandCause cause) {
        return true;
    }

    @Override
    @NotNull
    public Optional<Component> shortDescription(@NotNull CommandCause cause) {
        return Optional.of(Component.text("Base command for PacketEventsDetector."));
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.of(Component.text("Base command for PacketEventsDetector."));
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.text("/packeteventsdetector");
    }
}
