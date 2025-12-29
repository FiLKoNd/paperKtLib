package com.filkond.paperktlib.config.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    private const val INDEX0 = 0
    private const val INDEX1 = 1
    private const val INDEX2 = 2
    private const val INDEX3 = 3
    private const val INDEX4 = 4
    private const val INDEX5 = 5

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Location") {
            element<String>("world")
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
            element<Float>("yaw")
            element<Float>("pitch")
        }

    override fun serialize(encoder: Encoder, value: Location) {
        val structure = encoder.beginStructure(descriptor)
        structure.encodeStringElement(descriptor, 0, value.world.name)
        structure.encodeDoubleElement(descriptor, 1, value.x)
        structure.encodeDoubleElement(descriptor, 2, value.y)
        structure.encodeDoubleElement(descriptor, 3, value.z)
        structure.encodeFloatElement(descriptor, 4, value.yaw)
        structure.encodeFloatElement(descriptor, 5, value.pitch)
        structure.endStructure(descriptor)
    }
    override fun deserialize(decoder: Decoder): Location {
        return decoder.decodeStructure(descriptor) {
            var world = ""
            var x = 0.0
            var y = 0.0
            var z = 0.0
            var yaw = 0.0F
            var pitch = 0.0F

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    INDEX0 -> world = decodeStringElement(descriptor, INDEX0)
                    INDEX1 -> x = decodeDoubleElement(descriptor, INDEX1)
                    INDEX2 -> y = decodeDoubleElement(descriptor, INDEX2)
                    INDEX3 -> z = decodeDoubleElement(descriptor, INDEX3)
                    INDEX4 -> yaw = decodeFloatElement(descriptor, INDEX4)
                    INDEX5 -> pitch = decodeFloatElement(descriptor, INDEX5)
                    -1 -> break // End of structure
                    else -> error("Unexpected index: $index")
                }
            }
            Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
        }
    }
}