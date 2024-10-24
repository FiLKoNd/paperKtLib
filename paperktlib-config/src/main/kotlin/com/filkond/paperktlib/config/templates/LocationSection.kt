package com.filkond.paperktlib.config.templates

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class LocationSection(
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {
    fun toLocation(): Location = Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
}