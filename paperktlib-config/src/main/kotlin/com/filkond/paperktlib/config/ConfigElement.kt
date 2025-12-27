package com.filkond.paperktlib.config

import java.io.File
import kotlin.reflect.KClass

data class ConfigElement<C : Config>(
    val clazz: KClass<@UnsafeVariance C>,
    val config: C,
    val file: File
)