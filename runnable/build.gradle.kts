plugins {
    packeteventsdetector.`java-conventions`
}

dependencies {
    implementation(project(":common"))
    annotationProcessor(libs.lombok)
}
