@file:Suppress("FunctionName")

package com.filkond.paperktlib.config.ext

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.ReloadableConfig
import com.filkond.paperktlib.config.manager.ConfigManager
import com.filkond.paperktlib.config.manager.SimpleConfigManager
import com.filkond.paperktlib.config.serializers.UUIDSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
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
fun <C : Config> SimpleConfigManager.load(configFileName: String, clazz: KClass<C>, instance: C? = null): C =
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
inline fun <reified C : Config> SimpleConfigManager.loadCompanion(configFileName: String): C =
    load(configFileName, C::class, C::class.companionObjectInstance as C)

@Suppress("UNCHECKED_CAST")
val ConfigManager.reloadableConfigs
    get() = configsElements.filter { it.config is ReloadableConfig } as Set<ConfigElement<ReloadableConfig>>

fun ConfigManager.reloadAll() {
    reloadableConfigs.forEach {
        reload(it.clazz)
    }
}

fun ConfigManager.saveAll() {
    configsElements.forEach {
        save(it.clazz)
    }
}

fun ConfigManager.unloadAll() {
    configsElements.forEach {
        unload(it.clazz)
    }
}

@Suppress("UNCHECKED_CAST")
fun <C : Config> ConfigManager.getConfigElementByClass(clazz: KClass<C>): ConfigElement<C> =
    configsElements
        .filter { it.clazz == clazz }
        .firstOrNull {
            it.clazz == clazz
        } as? ConfigElement<C> ?: throw IllegalArgumentException("Config with class $clazz is not loaded.")

@OptIn(ExperimentalSerializationApi::class)
fun JsonConfigManager(configFolder: File) = SimpleConfigManager(configFolder, Json {
    encodeDefaults = true
    allowComments = true
    ignoreUnknownKeys = true
    prettyPrint = true
    serializersModule = paperKtLibSerializerModule()
})

fun YamlConfigManager(configFolder: File) = SimpleConfigManager(
    configFolder, Yaml(
        configuration = YamlConfiguration(encodeDefaults = true),
        serializersModule = paperKtLibSerializerModule()
    )
)

fun paperKtLibSerializerModule(): SerializersModule = SerializersModule {
    contextual(UUIDSerializer)
}

data class ConfigElement<out C : Config>(
    val clazz: KClass<out C>,
    val config: C,
    val file: File
)