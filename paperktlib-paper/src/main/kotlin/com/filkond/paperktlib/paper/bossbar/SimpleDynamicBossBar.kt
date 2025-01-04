package com.filkond.paperktlib.paper.bossbar

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

class SimpleDynamicBossBar(
    val uuid: UUID,
    var title: () -> Component,
    var progress: () -> Float,
    var shouldDisplay: () -> Boolean,
    var color: () -> BossBar.Color,
    var overlay: () -> BossBar.Overlay
) : DynamicBossBar{
    private constructor(builder: Builder) : this(
        uuid = UUID.randomUUID(),
        title = builder.title,
        progress = builder.progress,
        shouldDisplay = builder.shouldDisplay,
        color = builder.color,
        overlay = builder.overlay
    )

    val bossBar = BossBar.bossBar(title(), progress(), color(), overlay())

    override fun show(audience: Audience) = audience.showBossBar(bossBar)
    override fun hide(audience: Audience) = audience.hideBossBar(bossBar)

    override fun onRemove() = Bukkit.getOnlinePlayers().forEach { hide(it) }

    override fun update() {
        bossBar.name(title())
        bossBar.color(color())
        bossBar.overlay(overlay())
        bossBar.progress(progress().coerceIn(0.0f, 1.0f))

        bossBar.viewers().map {
            it as? Audience
        }.filterNotNull().forEach { if (shouldDisplay()) show(it) else hide(it) }
    }

    override fun equals(other: Any?): Boolean = other is SimpleDynamicBossBar && other.uuid == uuid
    override fun hashCode(): Int = uuid.hashCode()

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder() {
        var title: () -> Component = { Component.empty() }
        var progress: () -> Float = { 1.0f }
        var shouldDisplay: () -> Boolean = { true }
        var color: () -> BossBar.Color = { BossBar.Color.RED }
        var overlay: () -> BossBar.Overlay = { BossBar.Overlay.PROGRESS }

        fun build(): SimpleDynamicBossBar = SimpleDynamicBossBar(this)
    }
}