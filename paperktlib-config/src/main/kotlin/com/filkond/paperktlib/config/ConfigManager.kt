package com.filkond.paperktlib.config

import com.charleskorn.kaml.Yaml
import com.filkond.paperktlib.config.ext.loadConfigFromFileOrDefault
import com.filkond.paperktlib.config.ext.update
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ConfigManager(
    val folder: File,
    protected val formatter: StringFormat
) {
    protected val configs: MutableMap<File, Config> = mutableMapOf()

    inline fun <reified T : Config> config(fileName: String): T {
        val file = File(folder, fileName)
        return config(file, T::class)
    }

    fun <T : Config> config(file: File, clazz: KClass<T>): T {
        val config = loadConfig(file, clazz)
        configs[file] = config
        return config
    }

    @JvmName("reloadConfig")
    private inline fun <reified T : Config> T.reload() {
        configs.values.first { T::class == it::class }.update(this)
    }

    fun reload(config: Config) {
        config.reload()
    }

    fun reload(fileName: String) {
        val file = File(folder, fileName)
        configs[file]!!.update(file)
    }

    fun reloadAll() {
        configs.forEach { it.value.reload() }
    }

    private inline fun <reified T : Config> T.update(file: File) = loadConfig(file, T::class).update(this)
    private fun <T : Config> loadConfig(file: File, clazz: KClass<T>): T =
        loadConfigFromFileOrDefault(formatter, file, clazz) {
            clazz.createInstance()
        }
}
class JsonConfigManager(folder: File, json: Json = Json) : ConfigManager(folder, json)
class YamlConfigManager(folder: File, yaml: Yaml = Yaml()) : ConfigManager(folder, yaml)

interface Config