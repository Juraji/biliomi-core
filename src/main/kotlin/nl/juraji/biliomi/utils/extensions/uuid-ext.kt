package nl.juraji.biliomi.utils.extensions

import java.util.*

/**
 * Generates UUIDS
 *
 * @param fromNames 0 or more [String]s to base the UUID on
 * @return A v3 UUID based on the given names, or a v4 UUID when no names are given
 */
fun uuid(vararg fromNames: String?): String =
    if (fromNames.isEmpty()) UUID.randomUUID().toString()
    else fromNames
        .fold("", { acc, n -> acc + n })
        .let { UUID.nameUUIDFromBytes(it.toByteArray()) }
        .toString()
