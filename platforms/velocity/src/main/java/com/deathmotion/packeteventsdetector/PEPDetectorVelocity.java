package com.deathmotion.packeteventsdetector;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public class PEPDetectorVelocity {
    private final VelocityPEDetector peDetector;

    @Inject
    public PEPDetectorVelocity(ProxyServer server) {
        this.peDetector = new VelocityPEDetector(server);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
        peDetector.commonOnEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
        peDetector.commonOnDisable();
    }
}
