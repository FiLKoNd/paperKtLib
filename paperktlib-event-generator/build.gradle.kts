plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.ksp.api)
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)

    add("kspTest", project(":paperktlib-event-generator"))
}