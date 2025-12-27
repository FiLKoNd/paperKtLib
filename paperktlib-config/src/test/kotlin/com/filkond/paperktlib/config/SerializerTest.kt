package com.filkond.paperktlib.config

import be.seeseemelk.mockbukkit.MockBukkit
import com.filkond.paperktlib.config.serializers.LocationSerializer
import kotlinx.serialization.json.Json
import org.bukkit.Location
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SerializerTest {

    @BeforeEach
    fun setUp() {
        MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun testLocationSerializer() {
        val world = MockBukkit.getMock()!!.addSimpleWorld("world")
        val location = Location(world, 10.0, 20.0, 30.0, 45.0f, 90.0f)

        val json = Json.encodeToString(LocationSerializer, location)
        val deserializedLocation = Json.decodeFromString(LocationSerializer, json)

        assertEquals(location, deserializedLocation)
    }
}