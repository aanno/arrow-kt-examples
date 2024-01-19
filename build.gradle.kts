import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    // https://github.com/JLLeitschuh/ktlint-gradle
    // id("org.jlleitschuh.gradle.ktlint-idea") version "11.6.1"
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
    // for list, look at https://repo.maven.apache.org/maven2/io/gitlab/arturbosch/detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-complexity:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-coroutines:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-documentation:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-empty:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-exceptions:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-naming:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-performance:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-errorprone:1.23.3")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-style:1.23.3")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

kotlin {
    // should be 21 but detekt supports only 20 (tp)
    jvmToolchain(20)
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    baseline.set(file("config/ktlint/baseline.xml"))
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.PLAIN)
    }
    /*
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
     */
}

// https://github.com/detekt/detekt
detekt {
    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects. `false` by default.
    parallel = true

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    config.setFrom("config/detekt/detekt.yml")

    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false

    // Turns on all the rules. `false` by default.
    allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("config/detekt/baseline.xml")

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

    // stringLiteralDuplication
}

// https://detekt.dev/docs/gettingstarted/gradle/
tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = "20"
    // jdkHome.set(file("path/to/jdkHome"))
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "20"
}
// https://detekt.dev/docs/introduction/baseline
val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}
