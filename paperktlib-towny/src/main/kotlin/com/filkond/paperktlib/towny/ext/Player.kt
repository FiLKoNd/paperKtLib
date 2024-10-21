package com.filkond.paperktlib.ext

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.`object`.Nation
import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import org.bukkit.entity.Player

private val towny by lazy {
    TownyAPI.getInstance()
}

val Player.resident: Resident
    get() = towny.getResidentOrThrow(this)

val Player.town: Town?
    get() = resident.townOrNull

val Player.nation: Nation?
    get() = resident.nationOrNull