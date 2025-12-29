package com.filkond.paperktlib.config.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object InventorySerializer : KSerializer<Inventory> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Inventory", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Inventory) {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(value.size)

        // Save every element in the list
        value.contents.forEach { dataOutput.writeObject(it) }

        // Serialize that array
        dataOutput.close()
        encoder.encodeString(Base64Coder.encodeLines(outputStream.toByteArray()))
    }

    override fun deserialize(decoder: Decoder): Inventory {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(decoder.decodeString()))
        val dataInput = BukkitObjectInputStream(inputStream)
        val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

        // Read the serialized inventory
        for (i in 0 until inventory.size) {
            inventory.setItem(i, dataInput.readObject() as ItemStack?)
        }

        dataInput.close()
        return inventory
    }
}