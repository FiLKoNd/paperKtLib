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

fun Inventory.removeIfContains(stack: ItemStack): Boolean = containsAny(stack).also {
    if (it) removeForce(stack)
}

fun Inventory.removeIfContains(type: Material, amount: Int): Boolean = containsAny(type, amount).also {
    if (it) removeForce(type, amount)
}

fun Inventory.containsAny(type: Material, amount: Int): Boolean = all(type).values.sumOf { it.amount } >= amount
fun Inventory.containsAny(item: ItemStack): Boolean = all(item).values.sumOf { it.amount } >= item.amount

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

fun String.inventoryFromBase64(): Inventory {
    val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
    val dataInput = BukkitObjectInputStream(inputStream)
    val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

    // Read the serialized inventory
    for (i in 0 until inventory.size) {
        inventory.setItem(i, dataInput.readObject() as ItemStack?)
    }

    dataInput.close()
    return inventory
}