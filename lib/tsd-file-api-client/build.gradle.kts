plugins {
    id("java")
}

group = "elixir.no"
version = "2.0.0"


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("commons-io:commons-io:2.7")
    implementation("com.auth0:java-jwt:3.10.3")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.konghq:unirest-java:3.7.02")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.slf4j:slf4j-jdk14:1.7.28")

    testImplementation("junit:junit:4.13.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// TODO: Configure the publishing settings for distributing the library/application.
