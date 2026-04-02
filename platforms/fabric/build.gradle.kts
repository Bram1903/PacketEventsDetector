plugins {
    packeteventsdetector.`java-conventions`
}

repositories {
    maven {
        name = "fabric"
        url = uri("https://maven.fabricmc.net/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.fabric)
}
