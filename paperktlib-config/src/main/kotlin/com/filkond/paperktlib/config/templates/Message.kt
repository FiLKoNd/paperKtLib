package com.filkond.paperktlib.config.templates

import com.filkond.paperktlib.adventure.ext.deserialize
import io.papermc.paper.util.Tick
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.title.Title

@Serializable
sealed class Message {
    abstract val legacy: Boolean

    abstract fun send(audience: Audience, vararg resolvers: TagResolver)
}

@Serializable
@SerialName("chat")
data class ChatMessage(
    val text: String,
    override val legacy: Boolean = false
) : Message() {
    override fun send(audience: Audience, vararg resolvers: TagResolver) {
        audience.sendMessage(text.deserialize(*resolvers, isLegacy = legacy))
    }
}

@Serializable
@SerialName("action")
data class ActionMessage(
    val text: String,
    override val legacy: Boolean = false
) : Message() {
    override fun send(audience: Audience, vararg resolvers: TagResolver) {
        audience.sendActionBar(text.deserialize(*resolvers, isLegacy = legacy))
    }
}

@Serializable
@SerialName("title")
data class TitleMessage(
    val title: String,
    val subtitle: String,
    val fadeIn: Long = 3,
    val stay: Long = 10,
    val fadeOut: Long = 3,
    override val legacy: Boolean = false
) : Message() {
    override fun send(audience: Audience, vararg resolvers: TagResolver) {
        Title.title(
            title.deserialize(*resolvers, isLegacy = legacy),
            subtitle.deserialize(*resolvers, isLegacy = legacy),
            Title.Times.times(
                Tick.of(fadeIn),
                Tick.of(stay),
                Tick.of(fadeOut)
            )
        )
    }
}