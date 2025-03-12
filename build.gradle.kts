plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.3"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "maven-publish")

    group = "com.filkond"
    version = "1.3.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
    }

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        compileOnly(kotlin("reflect"))
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

        testImplementation(platform("org.junit:junit-bom:5.11.3"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        testImplementation(kotlin("reflect"))
        testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
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
        jvmToolchain(17)
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