package com.filkond.paperktlib.paper.ext

import org.bukkit.event.Cancellable

fun Cancellable.cancel() {
    isCancelled = true
}