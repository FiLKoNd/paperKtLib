plugins {
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(project(":paperktlib-adventure"))
    getParent()!!.apply {
        compileOnly(libs.kaml)
        compileOnly(libs.kotlinx.serialization.json)

        testImplementation(libs.kaml)
        testImplementation(libs.kotlinx.serialization.json)
    }
}