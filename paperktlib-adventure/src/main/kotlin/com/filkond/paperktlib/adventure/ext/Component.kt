package com.filkond.paperktlib.adventure.ext

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

private val mmSerializer = MiniMessage.miniMessage()
private val legacySerializer = LegacyComponentSerializer.legacyAmpersand()


fun String.deserialize(vararg resolvers: TagResolver, isLegacy: Boolean = false): Component =
    if (isLegacy) mmSerializer.deserialize(mmSerializer.serialize(legacySerializer.deserialize(this)), *resolvers) // <-- это пиздец
    else mmSerializer.deserialize(this, *resolvers)

fun <T : Collection<String>> T.deserialize(vararg resolvers: TagResolver, isLegacy: Boolean = false): List<Component> =
    map { it.deserialize(*resolvers, isLegacy = isLegacy) }

fun Component.serialize(isLegacy: Boolean = false): String =
    let { if (isLegacy) legacySerializer else mmSerializer }.serialize(this)

fun Collection<Component>.serialize(isLegacy: Boolean = false): Collection<String> = map { it.serialize(isLegacy) }

infix fun String.resolver(value: Any): TagResolver = Placeholder.parsed(this, value.toString())
infix fun String.resolver(value: Component): TagResolver = Placeholder.component(this, value)