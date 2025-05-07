package com.filkond.paperktlib.config

import kotlinx.serialization.Serializable

@Serializable
open class TestConfig2(
    var test: String = "no"
) : ReloadableConfig {
    companion object : TestConfig2()
}