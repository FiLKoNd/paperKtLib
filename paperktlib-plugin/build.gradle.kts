dependencies {
    implementation(project(":paperktlib-towny"))
    implementation(project(":paperktlib-paper"))
    implementation(project(":paperktlib-adventure"))
    implementation(project(":paperktlib-config"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}