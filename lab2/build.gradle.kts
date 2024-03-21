plugins {
    id("java")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    // Define the main class for the application.
    mainClass.set("pl.edu.agh.tw.knapp.Race")
}

tasks.test {
    useJUnitPlatform()
}