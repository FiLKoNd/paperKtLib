package com.filkond.paperktlib.config.ext

import com.filkond.paperktlib.config.templates.Sendable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun Audience.send(message: Sendable, vararg resolvers: TagResolver) = message.send(this, *resolvers)