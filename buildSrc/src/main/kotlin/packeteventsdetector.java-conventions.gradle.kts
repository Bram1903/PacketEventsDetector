plugins {
    java
}

group = rootProject.group
version = rootProject.version
description = project.description

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    disableAutoTargetJvm()
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 8
    }

    processResources {
        inputs.property("version", version)
        filesMatching(listOf("plugin.yml", "bungee.yml", "velocity-plugin.json")) {
            expand("version" to version)
        }
    }

    defaultTasks("build")
}
