plugins {
    id("kotlinx.benchmark").version("0.2.0-dev-20")
    kotlin("multiplatform")
}

project.group = rootProject.group
project.version = rootProject.version

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.apply {
                jvmTarget = "1.6"
            }
        }
    }
    js {
        nodejs()
        browser {
            testTask {
                useKarma {
                    usePhantomJS()
                }
            }
        }
    }
    linuxX64()

    sourceSets {
        all {
            languageSettings.apply {
                languageVersion = "1.4"
                apiVersion = "1.4"
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:0.2.0-dev-20")
                api(project(":kotlin-wheels"))
            }
        }
    }
}

benchmark {
    configurations {
        named("main") {
            iterations = 20
        }
    }
    targets {
        register("jvm")
        register("js")
        register("linuxX64")
    }
}