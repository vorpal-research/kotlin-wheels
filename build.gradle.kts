import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.net.URI

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id("org.jetbrains.kotlin.multiplatform").version("1.4.31")
    `maven-publish`
}

class Props {
    fun String.toUpperSnakeCase() = this.replace("([a-z])([A-Z])".toRegex(), "$1_$2").toUpperCaseAsciiOnly()
    operator fun getValue(self: Any?, prop: kotlin.reflect.KProperty<*>): String? =
            project.findProperty(prop.name)?.toString() ?: System.getenv(prop.name.toUpperSnakeCase())
}

val forceVersion by Props()

project.group = "ru.spbstu"
project.version = forceVersion ?: "0.0.1.1"

repositories {
    maven {
        url = URI("https://maven.pkg.github.com/vorpal-research/kotlin-maven")
        credentials {
            username = "vorpal-reseacher"
            password = "\u0031\u0030\u0062\u0037\u0064\u0066\u0031\u0032\u0063\u0064" +
                    "\u0035\u0034\u0038\u0037\u0034\u0065\u0030\u0034\u0035\u0035" +
                    "\u0038\u0031\u0063\u0039\u0039\u0062\u0031\u0066\u0032\u0030" +
                    "\u0038\u0065\u0031\u0061\u0035\u0033\u0065\u0036\u0032\u0038"
        }
    }
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
                implementation("ru.spbstu:kotlinx-warnings:1.4.31")
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
    }
}


val deployUsername by Props()
val deployPassword by Props()

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
