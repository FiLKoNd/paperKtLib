package com.filkond.paperktlib.event.test

import org.junit.jupiter.api.Test

class GeneratedGenerateEventCompilesTest {
    @Test
    fun `generated event class is visible to compiler`() {
        val evt = TestEvent(
            playerName = "Steve",
            pos1 = org.bukkit.Location(null, 0.0, 0.0, 0.0),
            pos2 = org.bukkit.Location(null, 1.0, 1.0, 1.0),
        )

        TestEvent.getHandlerList()

        evt.playerName
        evt.pos1
        evt.pos2
        evt.handlers

        evt.isCancelled
        evt.isCancelled = true
    }
}

