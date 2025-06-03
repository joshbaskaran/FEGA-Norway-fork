plugins {
    id("java")
    id("springboot-conventions")
    id("jsonwebtoken")
}

group = "no.elixir"

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
