package com.filkond.paperktlib.adventure.ext

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

fun Collection<Audience>.sendMessage(message: Component) {
    forEach { it.sendMessage(message) }
}