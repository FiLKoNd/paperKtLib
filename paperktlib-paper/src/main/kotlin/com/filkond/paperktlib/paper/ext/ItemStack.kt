package com.filkond.paperktlib.paper.ext

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack.applyMeta(meta: ItemMeta.() -> Unit): ItemStack = this.apply { editMeta(meta) }
fun <M : ItemMeta> ItemStack.applyMeta(metaClass: Class<M>,  meta: M.() -> Unit): ItemStack = this.apply { editMeta(metaClass, meta) }