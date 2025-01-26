@file:Suppress("FunctionName")

package com.filkond.paperktlib.config.ext

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.manager.ConfigManager
import com.filkond.paperktlib.config.manager.SimpleConfigManager
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KClass

fun <T : Config> SimpleConfigManager.load(configFileName: String, clazz: KClass<T>): T =
    load(File(configFolder, configFileName), clazz)

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

fun JsonConfigManager(configFolder: File) = SimpleConfigManager(configFolder, Json {
    encodeDefaults = true
    prettyPrint = true
})

fun YamlConfigManager(configFolder: File) = SimpleConfigManager(
    configFolder, Yaml(
        configuration = YamlConfiguration(encodeDefaults = true)
    )
)

typealias ConfigElement = Triple<KClass<out Config>, Config, File>