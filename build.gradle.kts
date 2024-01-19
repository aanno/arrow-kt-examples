plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"

    kotlin("jvm") version "1.9.22"
    // 1.9.22-1.0.16
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
    // https://detekt.dev/docs/gettingstarted/gradle/
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

val arrow_version: String by properties

group = "com.github.aanno.arrowkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // api(kotlin("stdlib"))

    implementation(platform("io.arrow-kt:arrow-stack:$arrow_version"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    ksp("io.arrow-kt:arrow-optics-ksp-plugin:$arrow_version")
    detektPlugins("com.wolt.arrow.detekt:rules:0.4.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    // should be 21 but detekt supports only 20 (tp)
    jvmToolchain(20)
}

ktlint {
    verbose.set(true)
    disabledRules.set(
        setOf(
            "comment-spacing",
            "filename",
            "import-ordering",
            "no-line-break-before-assignment",
            "standard:no-consecutive-blank-lines",
            "standard:trailing-comma-on-call-site",
            "standard:property-naming"
        )
    )
}

detekt {
    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects. `false` by default.
    parallel = true

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    config.setFrom("detekt/config.yml")

    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false

    // Turns on all the rules. `false` by default.
    allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("detekt/baseline.xml")

    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    disableDefaultRuleSets = false

    // Adds debug output during task execution. `false` by default.
    debug = false

    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = false

    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir.absolutePath
}
