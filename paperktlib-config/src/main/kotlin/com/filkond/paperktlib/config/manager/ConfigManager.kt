package com.filkond.paperktlib.config.manager

import com.filkond.paperktlib.config.Config
import com.filkond.paperktlib.config.ReloadableConfig
import com.filkond.paperktlib.config.ext.ConfigElement
import java.io.File
import kotlin.reflect.KClass

interface ConfigManager {
    /**
     * All stored config elements
     */
    val configsElements: Set<ConfigElement<Config>>

    /**
     * Register a config and add it to [configsElements]
     * @param configFile A config file
     * @param clazz A config class
     * @param instance A default instance of the config, if null, then be created from the primary constructor
     * @throws [IllegalArgumentException] If [configsElements] already contains a [clazz]
     * @return Instance of loaded config
     */
    fun <T : Config> load(configFile: File, clazz: KClass<T>, instance: T? = null): T

    /**
     * Reloads the config that corresponds to the [clazz] in the elements.
     * @param clazz A config class
     */
    fun <T : ReloadableConfig> reload(clazz: KClass<T>)

    /**
     * Update the contents of the config file, taking into account the content of the config instance
     * @throws [IllegalArgumentException] If [configsElements] not contains a [clazz]
     * @param clazz A config class
     */
    fun <T : Config> save(clazz: KClass<T>)

    /**
     * Removes a config from the [configsElements]
     * @throws [IllegalArgumentException] If [configsElements] not contains a [clazz]
     * @param clazz A config class
     */
    fun <T : Config> unload(clazz: KClass<T>)
}