plugins {
    packeteventsdetector.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "com.deathmotion.packeteventsdetector"
version = "1.0.0"

dependencies {
    implementation(project(":common"))
    implementation(project(":platforms:standalone"))
    implementation(project(":platforms:bukkit"))
    implementation(project(":platforms:velocity"))
    implementation(project(":platforms:bungeecord"))
    implementation(project(":platforms:sponge"))
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${version}.jar"
        archiveClassifier = null

        manifest {
            attributes(
                mapOf("Main-Class" to "com.deathmotion.packeteventsdetector.PEDetectorStandalone")
            )
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    val version = "1.21.11"

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }

        downloadPlugins {
            url("https://github.com/ViaVersion/ViaVersion/releases/download/5.8.1/ViaVersion-5.8.1.jar")
            url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.8.1/ViaBackwards-5.8.1.jar")
            url("https://cdn.modrinth.com/data/LJNGWSvH/versions/T0BdL6KY/grimac-bukkit-2.3.74-11d572f.jar")
        }

        jvmArgs = listOf(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}
