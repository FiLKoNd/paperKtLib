package com.filkond.paperktlib.config

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ConfigManagerTest {
    private val jsonConfigManager = JsonConfigManager(configFolder, Json {
        encodeDefaults = true
    })
    private val inputFile = File(configFolder, "input.json").apply { createNewFile() }

    @Test
    fun testLoad() {
        inputFile.writeText("""{"string":"hello"}""")
        val config = jsonConfigManager.load(inputFile, TestConfig::class)
        assert(config.string == "hello")
        inputFile.writeText("""{"string":"hello world!"}""")
        jsonConfigManager.reload(TestConfig::class)
        assert(config.string == "hello world!")
        config.string = "no"
        jsonConfigManager.saveAll()
        assert(inputFile.readText() == """{"string":"no"}""")
        jsonConfigManager.unloadAll()
    }

    companion object {
        @TempDir
        lateinit var configFolder: File
    }
}