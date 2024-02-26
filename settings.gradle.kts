pluginManagement {
    plugins {
        id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
        kotlin("jvm") version "1.9.22"
        id("com.google.devtools.ksp") version "1.9.22-1.0.17"
        id("io.gitlab.arturbosch.detekt") version "1.23.5"
        id("io.kotest") version "0.4.11"
        id("com.github.ben-manes.versions") version "0.51.0"
        id("se.patrikerdes.use-latest-versions") version "0.2.18"
        // needs kotlin version 1.8.10 (1.8.x)
        // id("io.arrow-kt.analysis.kotlin") version "2.0.2"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "arrow-kt-examples"
