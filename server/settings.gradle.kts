@file:Suppress("UnstableApiUsage")

rootProject.name = "detekt-lsp"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
    ":lsp-protocol",
    ":lsp-workspace",
    ":lsp-document",
    ":lsp-analysis",
    ":lsp-detekt-bridge",
    ":lsp-codeactions",
    ":lsp-perf",
    ":lsp-server-app",
)
