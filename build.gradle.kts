import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform").version("1.4.10").apply(false)
    `maven-publish`
}

allprojects {
    repositories {
        maven(url = "https://dl.bintray.com/vorpal-research/kotlin-maven")
        maven(url = "https://dl.bintray.com/kotlin/kotlinx" )
        jcenter()
    }
}

class Props {
    fun String.toUpperSnakeCase() = this.replace("([a-z])([A-Z])".toRegex(), "$1_$2").toUpperCaseAsciiOnly()
    operator fun getValue(self: Any?, prop: kotlin.reflect.KProperty<*>): String? =
            project.findProperty(prop.name)?.toString() ?: System.getenv(prop.name.toUpperSnakeCase())
}

val forceVersion by Props()

project.group = "ru.spbstu"
project.version = forceVersion ?: "0.0.1.0"
