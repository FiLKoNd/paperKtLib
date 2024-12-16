package com.filkond.paperktlib.config

import kotlinx.serialization.Serializable

@Serializable
data class TestConfig(
    var string: String = "just test"
) : Config