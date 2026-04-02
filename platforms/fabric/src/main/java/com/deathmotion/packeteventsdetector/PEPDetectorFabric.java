package com.deathmotion.packeteventsdetector;

import net.fabricmc.api.ModInitializer;

public class PEPDetectorFabric implements ModInitializer {
    private final FabricPEDetector peDetector = new FabricPEDetector();

    @Override
    public void onInitialize() {
        peDetector.commonOnEnable();
        Runtime.getRuntime().addShutdownHook(new Thread(peDetector::commonOnDisable, "pe-detector-fabric-shutdown"));
    }
}
