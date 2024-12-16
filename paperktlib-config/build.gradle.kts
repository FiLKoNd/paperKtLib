plugins {
    kotlin("plugin.serialization") version "2.0.21"
}

dependencies {
    compileOnly(project(":paperktlib-adventure"))
    compileOnly("com.charleskorn.kaml:kaml:0.66.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation("com.charleskorn.kaml:kaml:0.66.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}