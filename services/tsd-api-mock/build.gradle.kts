plugins {
    id("java")
    alias(libs.plugins.lombok)
    alias(libs.plugins.spring.boot)
}

group = "no.elixir"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    runtimeOnly("com.h2database:h2")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
}
