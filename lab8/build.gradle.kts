plugins {
    id("java")
    application
}

group = "pl.edu.agh.tw.knapp.lab8"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "pl.edu.agh.tw.knapp.lab8.Main"
}

tasks.test {
    useJUnitPlatform()
}