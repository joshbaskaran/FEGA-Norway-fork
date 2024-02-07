plugins {
    id("java")
    id("formatting-conventions")
}

group = "no.elixir.fega"
version = "1.0-SNAPSHOT"

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

tasks.register<Test>("testE2E") {
    group = "verification"
    useJUnitPlatform()
    dependsOn("startDockerContainers")
    finalizedBy("stopDockerContainers")
}

tasks.register<Exec>("startDockerContainers") {
    commandLine("docker-compose", "up", "-d")
}

tasks.register<Exec>("stopDockerContainers") {
    shouldRunAfter("testE2E")
    commandLine("docker-compose", "down")
}
