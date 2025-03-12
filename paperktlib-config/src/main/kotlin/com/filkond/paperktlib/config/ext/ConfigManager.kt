@file:Suppress("FunctionName")

package com.filkond.paperktlib.config.ext

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.manager.ConfigManager
import com.filkond.paperktlib.config.manager.SimpleConfigManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

/**
 * Loads the config and link it to the [instance]
 * @receiver SimpleConfigManager
 * @param configFileName File name
 * @param clazz Config class
 * @param instance Instance to link
 * @return Instance of the loaded config
 */
fun <T : Config> SimpleConfigManager.load(configFileName: String, clazz: KClass<T>, instance: T? = null): T =
    load(File(configFolder, configFileName), clazz, instance)

/**
 * Loads the config
 * @receiver SimpleConfigManager
 * @param configFileName File name
 * @return Instance of the loaded config
 */
inline fun <reified T : Config> SimpleConfigManager.load(configFileName: String): T =
    load(configFileName, T::class, null)

/**
 * Loads the config and link it to the companion object
 * @receiver SimpleConfigManager
 * @param configFileName File name
 * @return Instance of the loaded config
 */
inline fun <reified T : Config> SimpleConfigManager.loadCompanion(configFileName: String): T =
    load(configFileName, T::class, T::class.companionObjectInstance as T)

fun ConfigManager.reloadAll() {
    configsElements.forEach {
        reload(it.first)
    }
}

fun ConfigManager.saveAll() {
    configsElements.forEach {
        save(it.first)
    }
}

fun ConfigManager.unloadAll() {
    configsElements.forEach {
        unload(it.first)
    }
}

fun <T : Config> ConfigManager.getConfigElementByClass(clazz: KClass<T>): ConfigElement =
    configsElements.firstOrNull {
        it.first == clazz
    } ?: throw IllegalArgumentException("Config with class $clazz is not loaded.")

@OptIn(ExperimentalSerializationApi::class)
fun JsonConfigManager(configFolder: File) = SimpleConfigManager(configFolder, Json {
    encodeDefaults = true
    allowComments = true
    ignoreUnknownKeys = true
    prettyPrint = true
})

fun YamlConfigManager(configFolder: File) = SimpleConfigManager(
    configFolder, Yaml(
        configuration = YamlConfiguration(encodeDefaults = true)
    )
)

typealias ConfigElement = Triple<KClass<out Config>, Config, File>