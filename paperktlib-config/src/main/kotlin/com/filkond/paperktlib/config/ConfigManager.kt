package com.filkond.paperktlib.config

import com.charleskorn.kaml.Yaml
import com.filkond.paperktlib.config.ext.loadConfigOrDefault
import com.filkond.paperktlib.config.ext.update
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
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
    fun reload(clazz: KClass<out Config>) = getConfigByClass(clazz).let { (file, config) ->
        config.update(loadConfigOrDefault(file, config::class))
    }

    /**
     * Unload a config from config manager
     * @param clazz A config class
     */
    fun unload(clazz: KClass<out Config>) {
        configs.remove(getConfigByClass(clazz).first)
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

    /**
     * p
     * @param clazz A config class
     * @return Pair<File, Config>
     */
    fun getConfigByClass(clazz: KClass<out Config>): Pair<File, Config> {
        return configs.asSequence().firstOrNull { it.value::class == clazz }?.let {
            return it.toPair()
        } ?: throw IllegalArgumentException("Config ${clazz.simpleName} not found")
    }

    /**
     * Save a config
     * @param clazz A config class
     */
    fun save(clazz: KClass<out Config>) {
        val (file, config) = getConfigByClass(clazz)
        file.writeText(formatter.encodeToString(config))
    }

    fun saveAll() {
        configs.forEach { (file, config) -> file.writeText(formatter.encodeToString(config)) }
    }

    private fun <T : Config> loadConfigOrDefault(file: File, clazz: KClass<T>): T =
        loadConfigOrDefault(formatter, file, clazz) {
            clazz.createInstance()
        }
}

class JsonConfigManager(folder: File, json: Json = Json) : ConfigManager(folder, json)
class YamlConfigManager(folder: File, yaml: Yaml = Yaml()) : ConfigManager(folder, yaml)

interface Config