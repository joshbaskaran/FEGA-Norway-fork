plugins {
    id("java")
    id("com.diffplug.spotless") version "6.23.3"
}

group = "no.elixir"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test { useJUnitPlatform() }

allprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        format("misc") {
            target("**/*.gradle", ".gitattributes", ".gitignore")

            // define the steps to apply to those files
            trimTrailingWhitespace()
            indentWithTabs()  // or spaces. Takes an integer argument if you don't like 4
            endWithNewline()
        }
        java {
            importOrder()
            removeUnusedImports()
            cleanthat()
            googleJavaFormat()
            formatAnnotations()
        }
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            diktat()
        }
        kotlinGradle { diktat() }
    }
}
