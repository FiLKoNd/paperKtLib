package com.filkond.paperktlib.paper.ext

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun <M : ItemMeta> ItemStack.applyMeta(meta: M.() -> Unit): ItemStack =
    this.apply {
        editMeta {
            meta(this@applyMeta as M)
        }
    }