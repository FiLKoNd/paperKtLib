package com.filkond.paperktlib.config

import com.charleskorn.kaml.Yaml
import com.filkond.paperktlib.config.ext.loadConfigOrDefault
import com.filkond.paperktlib.config.ext.update
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ConfigManager(
    val folder: File,
    private val formatter: StringFormat
) {
    private val logger = LogManager.getLogger()
    val configs: MutableMap<File, Config> = mutableMapOf()

    /**
     * Load a config and add it to configs
     * @param fileName A name of config file
     * @return Loaded config
     */
    inline fun <reified T : Config> load(fileName: String): T {
        val file = File(folder, fileName)
        return load(file, T::class)
    }

    /**
     * Load a config and add it to configs
     * @param file A config file
     * @param clazz A config Class
     * @return Loaded config
     */
    fun <T : Config> load(file: File, clazz: KClass<T>): T {
        val config = loadConfigOrDefault(file, clazz)
        configs[file] = config
        return config
    }

    /**
     * Load a config from file and update its values
     * @param fileName A name of config file
     */
    fun reload(fileName: String) {
        val file = File(folder, fileName)
        reload(configs[file]!!::class)
    }

    /**
     * Load a config from file and update its values
     * @param clazz A config class
     */
    fun reload(clazz: KClass<out Config>) {
        configs.asSequence().firstOrNull { it.value::class == clazz }?.also {
            it.value.update(loadConfigOrDefault(it.key, it.value::class))
        } ?: logger.warn("Config ${clazz.simpleName} not found")
    }

    /**
     * Unload a config from config manager
     * @param clazz A config class
     */
    fun unload(clazz: KClass<out Config>) {
        configs.entries.find { it.value::class == clazz }?.key?.let {
            configs.remove(it)
        }
    }

    /**
     * Unload all configs from config manager
     */
    fun unloadAll() {
        configs.clear()
    }

    /**
     * Reload all configs
     */
    fun reloadAll() {
        configs.forEach { (_, config) ->
            reload(config::class)
        }
    }

    private fun <T : Config> loadConfigOrDefault(file: File, clazz: KClass<T>): T =
        loadConfigOrDefault(formatter, file, clazz) {
            clazz.createInstance()
        }
}

class JsonConfigManager(folder: File, json: Json = Json) : ConfigManager(folder, json)
class YamlConfigManager(folder: File, yaml: Yaml = Yaml()) : ConfigManager(folder, yaml)

interface Config