plugins {
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    getParent()!!.apply {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kaml)
        implementation(libs.kotlin.reflect)
    }

    implementation(project(":paperktlib-towny"))
    implementation(project(":paperktlib-paper"))
    implementation(project(":paperktlib-adventure"))
    implementation(project(":paperktlib-config"))
    implementation(project(":paperktlib-event-generator"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}