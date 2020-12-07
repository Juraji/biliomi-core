package nl.juraji.biliomi.utils.extensions

import java.util.*

fun uuidV3(namespace: String, name: String): String =
    UUID.nameUUIDFromBytes("$namespace$name".toByteArray()).toString()

fun uuidV4(): String = UUID.randomUUID().toString()
