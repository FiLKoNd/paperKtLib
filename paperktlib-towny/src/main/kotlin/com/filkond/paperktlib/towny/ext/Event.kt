package com.filkond.paperktlib.towny.ext

import com.palmergames.bukkit.towny.event.CancellableTownyEvent

fun <T : CancellableTownyEvent> T.silentCancel() {
    cancelMessage = ""
    isCancelled = true
}