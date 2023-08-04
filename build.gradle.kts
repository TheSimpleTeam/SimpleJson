plugins {
    application
    java
}

group = "fr.minemobs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("fr.minemobs.jsonparser.Main")
}

tasks.test {
    useJUnitPlatform()
}