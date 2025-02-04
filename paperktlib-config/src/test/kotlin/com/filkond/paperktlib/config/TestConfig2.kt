package com.filkond.paperktlib.config

import kotlinx.serialization.Serializable

@Serializable
open class TestConfig2(
    var test: String = "no"
) : Config {
    companion object : TestConfig2()
}