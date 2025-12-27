plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)
    apply(plugin = rootProject.libs.plugins.maven.publish.get().pluginId)

    group = "com.filkond"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
    }

    dependencies {
        compileOnly(rootProject.libs.kotlin.stdlib)
        compileOnly(rootProject.libs.kotlin.reflect)
        compileOnly(rootProject.libs.paper.api)

        testImplementation(platform(rootProject.libs.junit.bom))
        testImplementation(rootProject.libs.junit.jupiter)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)

        testImplementation(rootProject.libs.mockbukkit)
        testImplementation(rootProject.libs.paper.api)

        testImplementation(rootProject.libs.kotlin.stdlib)
        testImplementation(rootProject.libs.kotlin.reflect)
    }

    tasks.build {
        dependsOn("shadowJar")
    }

    tasks {
        val sourcesJar by creating(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
            destinationDirectory.set(file("$rootDir/target"))
        }

        val javadocJar by creating(Jar::class) {
            dependsOn.add(javadoc)
            archiveClassifier.set("javadoc")
            from(javadoc)
            destinationDirectory.set(file("$rootDir/target"))
        }
    }

    tasks.shadowJar {
        destinationDirectory.set(file("$rootDir/target"))
        archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn("sourcesJar", "javadocJar")
    }

    tasks.clean {
        delete("$rootDir/target")
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.test {
        useJUnitPlatform()
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/FiLKoNd/paperKtLib")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
        publications {
            create<MavenPublication>(project.name) {
                from(components["kotlin"])
                artifact(tasks["sourcesJar"])
                artifact(tasks["javadocJar"])
                artifactId = project.name
                groupId = project.group.toString()
                version = project.version.toString()
                pom {
                    developers {
                        developer {
                            id = "FiLKoNd"
                            email = "fil.yt.pass@gmail.com"
                        }
                    }
                }
            }
        }
    }
}