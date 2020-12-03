package nl.juraji.biliomi.configuration.security

import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

object Authorities {
    const val GROUPS_READ_ALL = "ROLE_GROUPS_READ_ALL"
    const val GROUPS_CREATE = "ROLE_GROUPS_CREATE"
    const val GROUPS_UPDATE = "ROLE_GROUPS_UPDATE"
    const val GROUPS_DELETE = "ROLE_GROUPS_DELETE"
    const val USERS_READ_ME = "ROLE_USERS_READ_ME"
    const val USERS_UPDATE_ME_USERNAME = "ROLE_USERS_UPDATE_ME_USERNAME"
    const val USERS_UPDATE_ME_PASSWORD = "ROLE_USERS_UPDATE_ME_PASSWORD"
    const val USERS_READ_ALL = "ROLE_USERS_READ_ALL"
    const val USERS_CREATE = "ROLE_USERS_CREATE"
    const val USERS_DELETE = "ROLE_USERS_DELETE"
    const val USERS_ADD_GROUP = "ROLE_USERS_ADD_GROUP"
    const val USERS_REMOVE_GROUP = "ROLE_USERS_REMOVE_GROUP"
    const val BANK_READ_ME = "ROLE_BANK_READ_ME"
    const val BANK_READ_ALL = "ROLE_BANK_READ_ALL"
    const val BANK_ADD_POINTS = "ROLE_BANK_ADD_POINTS"
    const val BANK_TAKE_POINTS = "ROLE_BANK_TAKE_POINTS"
    const val BANK_START_STOP_INTEREST = "ROLE_BANK_START_STOP_INTEREST"
    const val SSE_CONNECT = "ROLE_SSE_CONNECT"

    val all: List<String> by lazy {
        Authorities::class.memberProperties
            .filter { it.returnType.isSubtypeOf(String::class.starProjectedType) }
            .map { it.getter.call() as String }
    }
}
