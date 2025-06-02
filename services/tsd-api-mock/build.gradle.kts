plugins {
    id("java")
    id("springboot-conventions")
}

group = "no.elixir"

dependencies {
    runtimeOnly("com.h2database:h2")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
