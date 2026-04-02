package com.deathmotion.packeteventsdetector;

import com.google.inject.Inject;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("packeteventsdetector")
public class PEPDetectorSponge {
    private final SpongePEDetector peDetector;

    @Inject
    public PEPDetectorSponge() {
        this.peDetector = new SpongePEDetector();
    }

    @Listener
    public void onServerStart(StartedEngineEvent<Server> ignoredEvent) {
        peDetector.commonOnEnable();
    }

    @Listener
    public void onServerStop(StoppingEngineEvent<Server> ignoredEvent) {
        peDetector.commonOnDisable();
    }
}
