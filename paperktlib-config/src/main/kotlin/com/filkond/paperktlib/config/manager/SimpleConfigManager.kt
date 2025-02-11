package com.filkond.paperktlib.config.manager

import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.ext.ConfigElement
import com.filkond.paperktlib.config.ext.getConfigElementByClass
import com.filkond.paperktlib.config.ext.loadConfigOrDefault
import com.filkond.paperktlib.config.ext.update
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class SimpleConfigManager(
    val configFolder: File,
    private val formatter: StringFormat
) : ConfigManager {
    private val configs: MutableSet<ConfigElement> = mutableSetOf()
    override val configsElements: Set<ConfigElement>
        get() = configs.toSet()

    override fun <T : Config> load(configFile: File, clazz: KClass<T>, instance: T?): T {
        if (configs.map { it.first == clazz }.contains(true)) {
            throw IllegalArgumentException("This config is already loaded.")
        }

        val configInstance = instance ?: loadConfigOrDefault(formatter, configFile, clazz, clazz::createInstance)
        val element = Triple(clazz, configInstance, configFile)

        configs.add(element)
        configInstance.onLoad()

        return configInstance
    }

    override fun <T : Config> reload(clazz: KClass<T>) {
        val (_, config, file) = getConfigElementByClass(clazz)
        val createdInstance = loadConfigOrDefault(formatter, file, clazz, clazz::createInstance)

        config.preReload()
        config.update(createdInstance)
        config.postReload()
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Config> save(clazz: KClass<T>) {
        val (_, config, file) = getConfigElementByClass(clazz)
        file.writeText(
            @Suppress("UNCHECKED_CAST")
            formatter.encodeToString(
                clazz.serializer(),
                config as T
            )
        )
    }

    override fun <T : Config> unload(clazz: KClass<T>) {
        val element = getConfigElementByClass(clazz)
        element.second.onUnload()
        configs.remove(element)
    }
}