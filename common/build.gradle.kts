plugins {
    packeteventsdetector.`java-conventions`
    `java-library`
}

dependencies {
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}