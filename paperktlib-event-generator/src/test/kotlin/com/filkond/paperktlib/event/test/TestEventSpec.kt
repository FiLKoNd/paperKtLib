package com.filkond.paperktlib.event.test

import com.filkond.paperktlib.event.GenerateEvent
import org.bukkit.Location

@GenerateEvent(cancellable = true, name = "TestEvent")
class TestEventSpec(
    val playerName: String,
    var pos1: Location,
    val pos2: Location,
    vararg players: String
)

// Base event spec - will generate BaseEvent
@GenerateEvent(cancellable = false)
abstract class BaseEventSpec(
    val message: String,
    var count: Int,
)

// Child event spec - will generate ChildEvent that extends BaseEvent
@GenerateEvent(cancellable = true)
class ChildEventSpec(
    message: String,
    count: Int,
    val extraData: String,
    var mutableExtra: Double,
) : BaseEventSpec(message, count)

// Event spec with visibility modifiers - will generate VisibilityEvent
@GenerateEvent(cancellable = false)
open class VisibilityEventSpec(
    val publicField: String,           // default visibility (public)
    internal val internalField: Int,    // internal visibility
    protected val protectedField: Double, // protected visibility
    private val privateField: Boolean,  // private visibility (will be private in generated event)
)

