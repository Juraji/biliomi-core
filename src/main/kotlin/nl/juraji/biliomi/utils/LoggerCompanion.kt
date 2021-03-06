package nl.juraji.biliomi.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class LoggerCompanion(subjectClass: KClass<*>) {
    protected val logger: Logger = LoggerFactory.getLogger(subjectClass.java)
}
