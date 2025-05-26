plugins {
    id("java")
    id("springboot-conventions")
    id("jsonwebtoken")
}

group = "no.elixir"

dependencies {
    runtimeOnly("com.h2database:h2")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
}
