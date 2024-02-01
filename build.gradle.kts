import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

group = "com.github.aanno.arrowkt"
version = "0.0.1-SNAPSHOT"
description = "arrow-kt-examples"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    // https://github.com/JLLeitschuh/ktlint-gradle
    // https://pinterest.github.io/ktlint/latest/
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("jvm-test-suite")
    id("io.kotest")
    // needs kotlin version 1.8.10 (1.8.x)
    // id("io.arrow-kt.analysis.kotlin")
    idea
    wrapper
}

val arrowVersion: String by properties
val quiverVersion: String by properties
val arrowDetektVersion: String by properties
val inikioVersion: String by properties
val kotestVersion: String by properties
val detektVersion: String by properties

val test by testing.suites.existing(JvmTestSuite::class)
val defaultTests = test

val mavenUrl1: String by properties
val mavenUrl2: String by properties

repositories {
    // mavenLocal()
    // mavenCentral()
    maven {
        url = uri(mavenUrl1)
    }
    maven {
        url = uri(mavenUrl2)
    }
    flatDir {
        dir("lib")
    }
    // inikio - should be at end
    // maven(url = "https://jitpack.io")
}

idea {
    module {
        setDownloadJavadoc(true)
        setDownloadSources(true)
    }
}

// Strange HACK needed to use inikio-ksp:0.2-SNAPSHOT (tp)
val ksp by configurations.named("ksp") {
    extendsFrom(configurations.implementation.get())
    dependencies {
        implementation("com.squareup:kotlinpoet-ksp:1.16.0")
    }
}

dependencies {
    // api(kotlin("stdlib"))

    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")
    implementation("app.cash.quiver:lib:$quiverVersion")

    // https://kotest.io/docs/quickstart
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    // https://kotlinlang.org/api/latest/kotlin.test/
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    ksp("io.arrow-kt:arrow-optics-ksp-plugin:$arrowVersion")

    implementation("com.github.serras.inikio:inikio-core-jvm:$inikioVersion")
    ksp("com.github.serras.inikio:inikio-ksp:$inikioVersion")

    // https://github.com/woltapp/arrow-detekt-rules
    detektPlugins("com.wolt.arrow.detekt:rules:$arrowDetektVersion")
    // for list, look at https://repo.maven.apache.org/maven2/io/gitlab/arturbosch/detekt
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-complexity:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-coroutines:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-documentation:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-empty:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-exceptions:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-naming:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-performance:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-errorprone:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-style:$detektVersion")
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "8.5"
    }

    withType<Test> {
        useJUnitPlatform()
    }

    // https://docs.gradle.org/8.5/userguide/upgrading_version_8.html#test_task_default_classpath
    create<Test>("defaultTests") {
        testClassesDirs = files(defaultTests.map { it.sources.output.classesDirs })
        classpath = files(defaultTests.map { it.sources.runtimeClasspath })
    }

    // This ensures that detektMain (https://detekt.dev/docs/gettingstarted/type-resolution/) is run
    named("compileKotlin") {
        finalizedBy("detektMain")
    }

    // This ensures that detektTest
    named("compileTestKotlin") {
        finalizedBy("detektTest")
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
        }
    }
}

kotlin {
    // should be 21 but detekt supports only 17 (tp)
    jvmToolchain(17)
    // https://kotlinlang.org/docs/ksp-quickstart.html#make-ide-aware-of-generated-code
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
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
    jvmTarget = "17"
    // jdkHome.set(file("path/to/jdkHome"))
    // exclude ksp generated code (inikio)
    val mainKotlin = fileTree("src/main/kotlin").include("**/*.kt", "**/*.kts")
    val testKotlin = fileTree("src/test/kotlin").include("**/*.kt", "**/*.kts")
    // replace
    setSource(mainKotlin)
    // add
    source(testKotlin)
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
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
