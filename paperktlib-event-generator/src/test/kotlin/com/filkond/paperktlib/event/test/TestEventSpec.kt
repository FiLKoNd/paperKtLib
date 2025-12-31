package com.filkond.paperktlib.event.test

import com.filkond.paperktlib.event.GenerateEvent
import org.bukkit.Location

@GenerateEvent(cancellable = true, name = "TestEvent")
class TestEventSpec(
    val playerName: String,
    val pos1: Location,
    val pos2: Location,
)

