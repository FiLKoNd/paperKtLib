import org.gradle.kotlin.dsl.test

plugins {
    kotlin("jvm") version "2.0.20"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.filkond"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    }

    tasks.build {
        dependsOn("shadowJar")
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.test {
        useJUnitPlatform()
    }
}
