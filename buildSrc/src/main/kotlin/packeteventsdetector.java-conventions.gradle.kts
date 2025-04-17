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
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    disableAutoTargetJvm()
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 8
    }

    processResources {
        inputs.property("version", version)
        filesMatching(listOf("plugin.yml")) {
            expand("version" to version)
        }
    }

    defaultTasks("build")
}