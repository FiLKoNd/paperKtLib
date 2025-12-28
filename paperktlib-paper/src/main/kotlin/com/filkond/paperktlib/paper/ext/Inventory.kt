package com.filkond.paperktlib.paper.ext

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun Inventory.removeForce(type: Material, amount: Int) {
    var remainingAmount = amount
    contents.forEach {
        if (it?.type == type) {
            val amountToRemove = minOf(remainingAmount, it.amount)
            it.amount -= amountToRemove
            remainingAmount -= amountToRemove
            if (remainingAmount == 0) return
        }
    }
}

fun Inventory.removeForce(stack: ItemStack) {
    var remainingAmount = stack.amount
    contents.forEach {
        if (it?.isSimilar(stack) == true) {
            val amountToRemove = minOf(remainingAmount, it.amount)
            it.amount -= amountToRemove
            remainingAmount -= amountToRemove
            if (remainingAmount == 0) return
        }
    }
}

fun Inventory.removeIfContains(stack: ItemStack): Boolean = containsAtLeast(stack).also {
    if (it) removeForce(stack)
}

fun Inventory.removeIfContains(type: Material, amount: Int): Boolean = containsAtLeast(type, amount).also {
    if (it) removeForce(type, amount)
}

fun Inventory.containsAtLeast(type: Material, amount: Int): Boolean {
    var total = 0
    all(type).values.forEach {
        total += it.amount
        if (total >= amount) return true
    }
    return false
}

fun Inventory.containsAtLeast(item: ItemStack): Boolean {
    var total = 0
    all(item).values.forEach {
        total += it.amount
        if (total >= item.amount) return true
    }
    return false
}

fun Inventory.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    val dataOutput = BukkitObjectOutputStream(outputStream)

    // Write the size of the inventory
    dataOutput.writeInt(size)

    // Save every element in the list
    contents.forEach { dataOutput.writeObject(it) }

    // Serialize that array
    dataOutput.close()
    return Base64Coder.encodeLines(outputStream.toByteArray())
}

fun inventoryFromBase64(serializedInventory: String): Inventory {
    val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(serializedInventory))
    val dataInput = BukkitObjectInputStream(inputStream)
    val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

    // Read the serialized inventory
    for (i in 0 until inventory.size) {
        inventory.setItem(i, dataInput.readObject() as ItemStack?)
    }

    dataInput.close()
    return inventory
}