package com.filkond.paperktlib.bossbar

import net.kyori.adventure.audience.Audience

interface DynamicBossBar {
    fun show(audience: Audience)
    fun hide(audience: Audience)
    fun update()
    fun onRemove()
}

