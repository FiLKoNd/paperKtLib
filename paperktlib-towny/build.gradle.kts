plugins {
    kotlin("jvm")
}

repositories {
    maven("https://repo.glaremasters.me/repository/towny/") {
        name = "glaremasters repo"
    }
}

dependencies {
    compileOnly("com.palmergames.bukkit.towny:towny:0.100.3.0")
}