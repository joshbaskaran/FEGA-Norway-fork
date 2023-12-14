plugins {
    id("com.diffplug.spotless")
}

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