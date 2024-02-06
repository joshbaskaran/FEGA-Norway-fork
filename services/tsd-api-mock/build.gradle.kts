plugins {
    id("springboot-conventions")
    id("jsonwebtoken")
}

group = "no.elixir"
version = "1.0-SNAPSHOT"

dependencies {
    runtimeOnly("com.h2database:h2")
    implementation("org.apache.commons:commons-lang3:3.14.0")
}

tasks.test {
    environment("DURABLE_FILE_IMPORT", "./tmp/")
}
