import java.net.URI

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id("org.jetbrains.kotlin.multiplatform").version("1.4.10")
    `maven-publish`
}

val forceVersion: String? by project

project.group = "ru.spbstu"
project.version = forceVersion ?: "0.0.1.0"

repositories {
    maven(url = "https://dl.bintray.com/vorpal-research/kotlin-maven")
    jcenter()
}

kotlin {
    jvm()
    js {
        nodejs()
        browser()
    }

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
                implementation("ru.spbstu:kotlinx-warnings:1.4.10")
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
                implementation(kotlin("test"))
                implementation(kotlin("test-testng"))
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
    }
}

val bintrayOrg: String? by project
val bintrayRepo: String? by project

publishing {
    repositories {
        maven {
            url = URI("https://api.bintray.com/maven/${bintrayOrg}/${bintrayRepo}/${project.name}/;publish=1;override=1")
            credentials {
                val bintrayUsername: String? by project
                val bintrayPassword: String? by project
                val env = System.getenv().withDefault { null }
                val BINTRAY_USERNAME by env
                val BINTRAY_PASSWORD by env
                username = (bintrayUsername ?: BINTRAY_USERNAME)
                password = (bintrayPassword ?: BINTRAY_PASSWORD)
            }
        }
    }
}
