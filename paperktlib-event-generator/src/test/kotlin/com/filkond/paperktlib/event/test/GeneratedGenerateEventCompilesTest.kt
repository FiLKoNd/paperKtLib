package com.filkond.paperktlib.event.test

import org.bukkit.Location
import org.junit.jupiter.api.Test

class GeneratedGenerateEventCompilesTest {
    @Test
    fun `generated event class is visible to compiler`() {
        val evt = TestEvent(
            playerName = "Steve",
            pos1 = Location(null, 0.0, 0.0, 0.0),
            pos2 = Location(null, 1.0, 1.0, 1.0),
            players = arrayOf("Alex", "Herobrine"),
        )

        TestEvent.getHandlerList()

        evt.playerName
        evt.pos1 = Location(null, 0.0, 0.0, 1.0)
        evt.pos2
        evt.players // vararg becomes Array<String> property
        evt.handlers

        evt.isCancelled
        evt.isCancelled = true
    }

    @Test
    fun `child event class inherits from base event`() {
        val childEvt = ChildEvent(
            message = "Inherited message",
            count = 10,
            extraData = "extra",
            mutableExtra = 3.14,
        )

        ChildEvent.getHandlerList()

        // Access inherited properties
        childEvt.message
        childEvt.count = 20

        // Access child-specific properties
        childEvt.extraData
        childEvt.mutableExtra = 2.71
        childEvt.handlers

        // ChildEvent is cancellable
        childEvt.isCancelled
        childEvt.isCancelled = true
    }

    @Test
    fun `visibility modifiers are preserved in generated event`() {
        val evt = VisibilityEvent(
            publicField = "public",
            internalField = 42,
            protectedField = 3.14,
            privateField = true,
        )

        VisibilityEvent.getHandlerList()

        // Public field should be accessible
        evt.publicField

        // Internal field should be accessible in same module
        evt.internalField

        // Protected field - can only be accessed from subclass
        // (we can't test this directly here, but it should compile)

        // Private field - not accessible from outside
        // evt.privateField // This would not compile - commented out

        evt.handlers
    }
}

