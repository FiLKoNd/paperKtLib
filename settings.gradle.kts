pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "paperKtLib"
include(
    "paperktlib-plugin",
    "paperktlib-towny",
    "paperktlib-adventure",
    "paperktlib-config",
    "paperktlib-paper"
)