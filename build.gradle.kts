import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin.Companion.METADATA_TARGET_NAME
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URI

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("multiplatform").version("1.6.21")
    id("org.jetbrains.kotlinx.benchmark") version "0.4.2"
    `maven-publish`
    `java-library`
    jacoco
}

class Props {
    fun String.toUpperSnakeCase() = this.replace("([a-z])([A-Z])".toRegex(), "$1_$2").toUpperCaseAsciiOnly()
    operator fun getValue(self: Any?, prop: kotlin.reflect.KProperty<*>): String? =
            project.findProperty(prop.name)?.toString() ?: System.getenv(prop.name.toUpperSnakeCase())
}

val forceVersion by Props()

project.group = "ru.spbstu"
project.version = forceVersion ?: "0.0.1.4"

repositories {
    maven("https://maven.vorpal-research.science")
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.apply {
                jvmTarget = "1.6"
            }
        }
    }
    js(IR) {
        nodejs()
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    linuxX64()

    targets.all {
        if (name != METADATA_TARGET_NAME) compilations.maybeCreate("benchmarks")
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("ru.spbstu:kotlinx-warnings:${getKotlinPluginVersion()}")
            }
        }
        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
        }
        val jvmTest by getting {
            dependsOn(commonTest)
            dependsOn(jvmMain)
            dependencies {
                api("org.jetbrains.kotlin:kotlin-test-junit")
                implementation("com.google.guava:guava-testlib:18.0")
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
        }
        val jsTest by getting {
            dependsOn(commonTest)
            dependsOn(jsMain)
            dependencies {
                api(kotlin("test-js"))
            }
        }

        val linuxX64Main by getting {
            dependsOn(commonMain)
        }
        val linuxX64Test by getting {
            dependsOn(linuxX64Main)
        }

        val commonBenchmarks by creating {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.2")
                dependsOn(commonMain)
            }
        }
        val jvmBenchmarks by getting {
            dependsOn(jvmMain)
            dependsOn(commonBenchmarks)
        }
        val jsBenchmarks by getting {
            dependsOn(jsMain)
            dependsOn(commonBenchmarks)
        }
    }
}

jacoco {
    toolVersion = "0.8.6"
}

tasks.jacocoTestReport {
    val coverageSourceDirs = arrayOf(
        "src/commonMain",
        "src/jvmMain"
    )

    val classFiles = File("${buildDir}/classes/kotlin/jvm/")
        .walkBottomUp()
        .toSet()

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

    executionData
        .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

val deployUsername by Props()
val deployPassword by Props()

benchmark {
    configurations {
        val main by getting {
            mode = "avgt"
            reportFormat = "scsv"
            outputTimeUnit = "ns"
            iterationTime = 1
            iterations = 10
            warmups = 8
        }
    }

    targets {
        kotlin.targets.all {
            this.compilations.findByName("benchmarks")?.defaultSourceSetName?.let(::register)
        }
    }
}

publishing {
    repositories {
        maven {
            url = URI("https://maven.pkg.github.com/vorpal-research/kotlin-maven")
            credentials {
                username = deployUsername
                password = deployPassword
            }

        }
    }
}
