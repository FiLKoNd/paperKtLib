package com.filkond.paperktlib.config

import kotlinx.serialization.Serializable

@Serializable
open class TestConfig(
    var string: String = "вtest"
) : Config {
    companion object : TestConfig()
}