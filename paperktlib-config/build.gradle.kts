plugins {
    kotlin("plugin.serialization") version "2.0.21"
}

dependencies {
    compileOnly(project(":paperktlib-adventure"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}