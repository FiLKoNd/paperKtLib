package com.filkond.paperktlib.config.ext

import com.filkond.paperktlib.config.templates.Sendable
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun Audience.send(message: Sendable, vararg resolvers: TagResolver) = message.send(this, *resolvers)
fun <T : Audience> Iterable<T>.send(message: Sendable, vararg resolvers: TagResolver) {
    forEach {
        it.send(message, *resolvers)
    }
}

fun <T : Audience> Iterable<T>.send(message: Sendable, resolvers: ResolversBuilder.(audience: T) -> Unit) {
    forEach { audience ->
        ResolversBuilder().apply {
            resolvers(this, audience)
            audience.send(message, *build())
        }
    }
}

class ResolversBuilder {
    private val resolvers: MutableList<TagResolver> = arrayListOf()
    infix fun String.resolver(value: Any) {
        resolvers.add((value as? Component)?.let { Placeholder.component(this, value) } ?: Placeholder.parsed(
            this,
            value.toString()
        ))
    }

    fun build(): Array<TagResolver> = resolvers.toTypedArray()
}