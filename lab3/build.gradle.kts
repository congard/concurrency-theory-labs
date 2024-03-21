plugins {
    id("java")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    // Define the main class for the application.
    if (project.hasProperty("main")) {
        mainClass.set(project.properties["main"].toString())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}