package com.filkond.paperktlib.config

interface ReloadableConfig : Config {
    /**
     * Called before reloading the config
     */
    fun preReload() {}

    /**
     * Called after reloading the config
     */
    fun postReload() {}
}