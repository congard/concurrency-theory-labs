plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // https://mvnrepository.com/artifact/org.codehaus.jcsp/jcsp
    implementation("org.codehaus.jcsp:jcsp:1.1-rc5")
}

tasks.test {
    useJUnitPlatform()
}