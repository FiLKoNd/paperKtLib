plugins {
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(project(":paperktlib-adventure"))
    getParent()!!.apply {
        compileOnly(libs.kaml)
        compileOnly(libs.kotlinx.serialization.json)

        implementation("net.kyori:adventure-api:4.26.1")
        implementation("net.kyori:adventure-text-minimessage:4.26.1")

        implementation("net.kyori:adventure-platform-bukkit:4.4.1")

        testImplementation(libs.kaml)
        testImplementation(libs.kotlinx.serialization.json)
    }
}