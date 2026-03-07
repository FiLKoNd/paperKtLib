package com.filkond.paperktlib.config.templates

import com.filkond.paperktlib.adventure.ext.deserialize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.title.Title
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

@Serializable
sealed class Message : Sendable {
    abstract val legacy: Boolean
}

@Serializable
sealed interface Sendable {
    fun send(audience: Audience, vararg resolvers: TagResolver)
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

const val TICK_IN_MILLIS: Long = 50L

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
                (fadeIn * TICK_IN_MILLIS).toDuration(DurationUnit.MILLISECONDS).toJavaDuration(),
                (stay * TICK_IN_MILLIS).toDuration(DurationUnit.MILLISECONDS).toJavaDuration(),
                (fadeOut * TICK_IN_MILLIS).toDuration(DurationUnit.MILLISECONDS).toJavaDuration()
            )
        )
    }
}

@Serializable
@SerialName("multimessage")
data class MultiMessage(
    val list: List<Message>
) : Sendable {
    override fun send(audience: Audience, vararg resolvers: TagResolver) {
        list.forEach {
            it.send(audience, *resolvers)
        }
    }
}