plugins {
    packeteventsdetector.`java-conventions`
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.bungeecord)
    annotationProcessor(libs.lombok)
}
