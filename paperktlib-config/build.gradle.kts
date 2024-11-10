plugins {
    kotlin("plugin.serialization") version "2.1.0-Beta1"
}

dependencies {
    implementation(project(":paperktlib-adventure"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}