package com.filkond.paperktlib.config.manager

import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.ReloadableConfig
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
    private val configs: MutableSet<ConfigElement<Config>> = mutableSetOf()
    override val configsElements: Set<ConfigElement<Config>>
        get() = configs.toSet()

    override fun <T : Config> load(configFile: File, clazz: KClass<T>, instance: T?): T {
        if (configs.map { it.clazz == clazz }.contains(true)) {
            throw IllegalArgumentException("This config is already loaded.")
        }

        val loadedConfig = loadConfigOrDefault(formatter, configFile, clazz, clazz::createInstance)
        val configInstance = instance?.apply { update(loadedConfig) } ?: loadedConfig
        val element = ConfigElement(clazz, configInstance, configFile)

        configs.add(element)
        configInstance.onLoad()

        return configInstance
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ReloadableConfig> reload(clazz: KClass<T>) {
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
        element.config.onUnload()
        configs.remove(element)
    }
}