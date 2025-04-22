plugins {
    base
}

val goBuild = tasks.register("goBuild", Exec::class) {
    description = "Build the Go application"
    commandLine("go", "build", "-o", "build/")
}

tasks.named("build") {
    dependsOn(goBuild)
}

tasks.register("test", Exec::class) {
    group = "verification"
    description = "Test the Go application"
    commandLine("go", "test")
}

val goClean = tasks.register("goClean", Exec::class) {
    description = "Deletes the build directory"
    commandLine("go", "clean")
}

tasks.named("clean") {
    dependsOn(goClean)
}
