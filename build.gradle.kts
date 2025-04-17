plugins {
    packeteventsdetector.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "com.deathmotion.packeteventsdetector"
version = "1.0.0"

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
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
                mapOf("Main-Class" to "com.deathmotion.packeteventsdetector.PEDetectorRunnable")
            )
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    val version = "1.21.4"

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }

        downloadPlugins {
            url("https://github.com/ViaVersion/ViaVersion/releases/download/5.3.2/ViaVersion-5.3.2.jar")
            url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.3.2/ViaBackwards-5.3.2.jar")
            url("https://cdn.modrinth.com/data/LJNGWSvH/versions/4CqWKZph/grimac-2.3.71.jar")
        }

        jvmArgs = listOf(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}
