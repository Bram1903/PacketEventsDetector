pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "PacketEventsDetector"
include(":common")
include("platforms:standalone")
include(":platforms:bukkit")
include(":platforms:velocity")
include(":platforms:bungeecord")
include(":platforms:sponge")