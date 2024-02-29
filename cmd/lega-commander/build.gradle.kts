plugins {
    base
}

version = "1.1.0"

val goBuild = tasks.register("go-build", Exec::class) {
    description = "Build the LEGA Commander application"
    commandLine("go", "build", "-o", "build/")
}

tasks.named("build") {
    dependsOn("go-build")
}

tasks.register("test", Exec::class) {
    group = "verification"
    description = "Test the LEGA Commander application"
    commandLine("go", "test", "./...")
}

val goClean = tasks.register("go-clean", Exec::class) {
    description = "Deletes the build directory"
    commandLine("go", "clean")
}

tasks.named("clean") {
    dependsOn("go-clean")
}