pluginManagement {
    val mavenUrl1: String by settings
    val mavenUrl2: String  by settings

    repositories {
        // gradlePluginPortal()
        maven {
            url = uri(mavenUrl1)
        }
        maven {
            url = uri(mavenUrl2)
        }
    }

    plugins {
        id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
        kotlin("jvm") version "1.9.22"
        id("com.google.devtools.ksp") version "1.9.22-1.0.16"
        id("io.gitlab.arturbosch.detekt") version "1.23.3"
        id("io.kotest") version "0.4.11"
        // needs kotlin version 1.8.10 (1.8.x)
        // id("io.arrow-kt.analysis.kotlin") version "2.0.2"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "arrow-kt-examples"
