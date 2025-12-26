plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    maven("https://repo.glaremasters.me/repository/towny/") {
        name = "glaremasters repo"
    }
}

dependencies {
    compileOnly(libs.towny)
}