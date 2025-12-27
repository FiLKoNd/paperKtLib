@file:OptIn(InternalSerializationApi::class)

package com.filkond.paperktlib.config.ext

import com.filkond.paperktlib.config.Config
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import org.apache.logging.log4j.LogManager
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

private val logger = LogManager.getLogger()
private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd;HH-mm-ss")

fun <T : Config> T.update(newObject: T) {
    val clazz = this::class
    clazz.members.filterIsInstance<KMutableProperty<*>>().forEach {
        it.setter.call(this, it.getter.call(newObject))
    }
}

fun <T : Config> loadConfigOrDefault(
    formatter: StringFormat,
    file: File,
    clazz: KClass<T>,
    getDefault: () -> T
): T {
    return try {
        formatter.decodeFromString(clazz.serializer(), file.readText())
    } catch (e: Exception) {
        logger.error("Failed to load config ${file.name}, using default: $e")
        if (file.exists()) {
            createBackup(file)
        }
        writeAndGetDefaultConfig(formatter, file, getDefault, clazz)
    }
}

private fun createBackup(file: File): File {
    val timestamp = LocalDateTime.now().format(timeFormat)
    val backupFile = File(
        file.parentFile,
        "${file.nameWithoutExtension}-backup-$timestamp-.${file.extension}"
    )
    file.copyTo(backupFile)
    logger.info("Backup created at: ${backupFile.path}")
    return backupFile
}

private fun <T : Config> writeAndGetDefaultConfig(
    formatter: StringFormat,
    file: File,
    getDefault: () -> T,
    clazz: KClass<T>
): T {
    file.parentFile.mkdirs()
    file.createNewFile()
    return getDefault().also {
        file.writeText(formatter.encodeToString(clazz.serializer(), it))
    }
}