import org.spongepowered.gradle.plugin.config.PluginLoaders

plugins {
    packeteventsdetector.`java-conventions`
    alias(libs.plugins.spongeGradle)
}

repositories {
    maven {
        name = "sponge"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.sponge)
    annotationProcessor(libs.lombok)
}

sponge {
    apiVersion("8.1.0")
    license("GPL3")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("packeteventsdetector") {
        displayName("PacketEventsDetector")
        entrypoint("com.deathmotion.packeteventsdetector.PEPDetectorSponge")
        description("Plugin that tries to detect plugins that shade PacketEvents.")
        version(project.version.toString())
        contributor("Bram") {
            description("Author")
        }
    }
}
