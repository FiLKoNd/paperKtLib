val paperVersion = "1.21.4-R0.1-SNAPSHOT"

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

