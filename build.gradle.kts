import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
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

class Props {
    fun String.toUpperSnakeCase() = this.replace("([a-z])([A-Z])".toRegex(), "$1_$2").toUpperCaseAsciiOnly()
    operator fun getValue(self: Any?, prop: kotlin.reflect.KProperty<*>): String? =
            project.findProperty(prop.name)?.toString() ?: System.getenv(prop.name.toUpperSnakeCase())
}

val forceVersion by Props()

project.group = "ru.spbstu"
project.version = forceVersion ?: "0.0.1.0"

repositories {
    maven(url = "https://dl.bintray.com/vorpal-research/kotlin-maven")
    jcenter()
}

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


val bintrayOrg by Props()
val bintrayRepo by Props()
val bintrayUsername by Props()
val bintrayPassword by Props()

publishing {
    repositories {
        maven {
            url = URI("https://api.bintray.com/maven/${bintrayOrg}/${bintrayRepo}/${project.name}/;publish=1;override=1")
            credentials {
                username = bintrayUsername
                password = bintrayPassword
            }
        }
    }
}
