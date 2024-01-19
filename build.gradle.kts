import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    // https://github.com/JLLeitschuh/ktlint-gradle
    // https://pinterest.github.io/ktlint/latest/
    // id("org.jlleitschuh.gradle.ktlint-idea") version "11.6.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"

    kotlin("jvm") version "1.9.22"
    // 1.9.22-1.0.16
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
    // https://detekt.dev/docs/gettingstarted/gradle/
    id("io.gitlab.arturbosch.detekt") version "1.23.3"
}

val arrowVersion: String by properties

group = "com.github.aanno.arrowkt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // api(kotlin("stdlib"))

    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")

    // https://kotest.io/docs/quickstart
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.7.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-property:5.7.2")
    // https://kotlinlang.org/api/latest/kotlin.test/
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    ksp("io.arrow-kt:arrow-optics-ksp-plugin:$arrowVersion")

    // https://github.com/woltapp/arrow-detekt-rules
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
    // This ensures that detektMail (https://detekt.dev/docs/gettingstarted/type-resolution/) is run
    named("compileKotlin") {
        finalizedBy("detektMain")
    }
    named("compileTestKotlin") {
        finalizedBy("detektTest")
    }
}

kotlin {
    // should be 21 but detekt supports only 20 (tp)
    jvmToolchain(20)
}

// https://github.com/JLLeitschuh/ktlint-gradle
ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    // if true, only warnings (build will not fail)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
    // change with: `./gradlew ktlintGenerateBaseline`
    baseline.set(file("config/ktlint/baseline.xml"))
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.HTML)
        reporter(ReporterType.SARIF)
    }
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
    // change with: `./gradlew detektProjectBaseline`
    // change with: `./gradlew detektBaselineMain`
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
}

// https://detekt.dev/docs/gettingstarted/gradle/
tasks.withType<Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // checkstyle like format mainly for integrations like Jenkins
        xml.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)
        // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        sarif.required.set(true)
        // simple Markdown format
        md.required.set(true)
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
