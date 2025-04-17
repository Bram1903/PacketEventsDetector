plugins {
    packeteventsdetector.`java-conventions`
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paper)
    annotationProcessor(libs.lombok)
}