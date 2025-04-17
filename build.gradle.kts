plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.packetevents.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    disableAutoTargetJvm()
}

group = "com.deathmotion.packeteventsdetector"
version = "1.0.0-SNAPSHOT"

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${rootProject.version}.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17
    }

    defaultTasks("build")

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching(listOf("plugin.yml")) {
            expand(
                "version" to rootProject.version,
            )
        }
    }

    val version = "1.21.5"

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)

        }

        downloadPlugins {
            url("https://github.com/ViaVersion/ViaVersion/releases/download/5.3.2/ViaVersion-5.3.2.jar")
            url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.3.2/ViaBackwards-5.3.2.jar")
            url("https://cdn.modrinth.com/data/LJNGWSvH/versions/ePa255As/grimac-2.3.70.jar")
        }

        jvmArgs = listOf(
            "-Dcom.mojang.eula.agree=true"
        )
    }
}