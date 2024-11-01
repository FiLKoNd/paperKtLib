package com.filkond.paperktlib.config.ext

import com.filkond.paperktlib.config.templates.Message
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun Audience.send(message: Message, vararg resolvers: TagResolver) = message.send(this, *resolvers)