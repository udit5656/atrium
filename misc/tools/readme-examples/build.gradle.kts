plugins {
    id("build-logic.kotlin-jvm")
}

description = "Runs examples, includes the code and the output in README.md"

kotlin {
    sourceSets {
        main {
            dependencies {
                implementation(libs.junit.platform.console)
                implementation(libs.spek.jvm)
                implementation(libs.spek.runner)
                implementation(libs.spek.runtime)
                runtimeOnly(kotlin("reflect"))

                implementation(prefixedProject("fluent"))
                implementation(libs.niok)
            }
        }
        configureEach {
            languageSettings.apply {
                languageVersion = "1.8"
                apiVersion = "1.8"
            }
        }
    }
    tasks {
        register<JavaExec>("readme") {
            group = "documentation"
            description = "Runs examples, includes the code and the output in README.md"

            classpath = project.sourceSets.main.get().runtimeClasspath
            val version = rootProject.version.toString()
            environment("README_SOURCETREE", if (version.endsWith("-SNAPSHOT")) "tree/main" else "tree/v$version")

            this.mainClass.set("org.junit.platform.console.ConsoleLauncher")
            args = listOf(
                "--scan-class-path", project.sourceSets.main.get().output.classesDirs.asPath,
                "--disable-banner",
                "--fail-if-no-tests",
                "--include-engine", "spek2-readme",
                "--details", "summary"
            )
        }
        check {
            dependsOn("readme")
        }
    }
}
