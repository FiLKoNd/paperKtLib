package com.filkond.paperktlib.config

interface Config {
    /**
     * Called after loading the config
     */
    fun onLoad() {}

    /**
     * Called before unloading the config
     */
    fun onUnload() {}
}