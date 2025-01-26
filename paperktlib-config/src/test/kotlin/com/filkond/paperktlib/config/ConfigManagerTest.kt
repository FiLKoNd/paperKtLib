package com.filkond.paperktlib.config

import com.filkond.paperktlib.config.ext.saveAll
import com.filkond.paperktlib.config.ext.unloadAll
import com.filkond.paperktlib.config.manager.ConfigManager
import com.filkond.paperktlib.config.manager.SimpleConfigManager
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ConfigManagerTest {
    private lateinit var jsonConfigManager: ConfigManager
    private val inputFile = File(configFolder, "input.json").apply(File::createNewFile)

    @BeforeEach
    fun beforeTest() {
        jsonConfigManager = SimpleConfigManager(configFolder, Json {
            encodeDefaults = true
        })
    }

    @Test
    fun testLoad() {
        inputFile.writeText("""{"string":"hello"}""")
        jsonConfigManager.load(inputFile, TestConfig::class)
        assert(jsonConfigManager.configsElements.size == 1)
    }

    @Test
    fun testReload() {
        inputFile.writeText("""{"string":"hello"}""")
        val config = jsonConfigManager.load(inputFile, TestConfig::class)
        inputFile.writeText("""{"string":"hello world!"}""")
        jsonConfigManager.reload(TestConfig::class)

        assert(config.string == "hello world!")
    }

    @Test
    fun testCompanionReload() {
        inputFile.writeText("""{"string":"hello"}""")
        jsonConfigManager.load(inputFile, TestConfig::class, TestConfig.Companion)
        inputFile.writeText("""{"string":"hello world!"}""")
        jsonConfigManager.reload(TestConfig::class)

        assert(TestConfig.string == "hello world!")
    }

    @Test
    fun testSave() {
        inputFile.writeText("""{"string":"hello"}""")
        val config = jsonConfigManager.load(inputFile, TestConfig::class)

        config.string = "no"
        jsonConfigManager.saveAll()
        println(inputFile.readText())
        assert(inputFile.readText() == """{"string":"no"}""")

        jsonConfigManager.unloadAll()
    }

    @Test
    fun testUnload() {
        inputFile.writeText("""{"string":"hello"}""")
        jsonConfigManager.load(inputFile, TestConfig::class)
        jsonConfigManager.unloadAll()
        assert(jsonConfigManager.configsElements.isEmpty())
    }

    companion object {
        @TempDir
        lateinit var configFolder: File
    }
}