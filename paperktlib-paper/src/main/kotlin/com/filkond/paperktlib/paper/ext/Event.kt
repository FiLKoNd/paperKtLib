package com.filkond.paperktlib.paper.ext

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event

fun Cancellable.cancel() {
    isCancelled = true
}

fun Event.invoke() = Bukkit.getPluginManager().callEvent(this)