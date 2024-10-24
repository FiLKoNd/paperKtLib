plugins {
    kotlin("plugin.serialization") version "2.0.20"
}

dependencies {
    implementation(project(":paperktlib-adventure"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}