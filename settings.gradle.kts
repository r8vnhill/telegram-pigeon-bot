rootProject.name = "telegram-pigeon-bot"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version extra["kotlin.version"] as String
        id("io.gitlab.arturbosch.detekt") version extra["detekt.version"] as String
        id("org.jetbrains.dokka") version extra["dokka.version"] as String
    }
}