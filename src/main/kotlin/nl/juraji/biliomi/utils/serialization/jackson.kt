package nl.juraji.biliomi.utils.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlin.reflect.KClass

fun <T : Any> SimpleModule.serializer(type: KClass<T>, block: JsonGenerator.(T, SerializerProvider) -> Unit) {
    this.addSerializer(object : StdSerializer<T>(type.java) {
        override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) = block(gen, value, serializers)
    })
}

fun <T : Any> SimpleModule.keySerializer(type: KClass<T>, block: JsonGenerator.(T, SerializerProvider) -> Unit) {
    this.addKeySerializer(type.java, object : StdSerializer<T>(type.java) {
        override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) = block(gen, value, serializers)
    })
}

fun <T : Any> SimpleModule.deserializer(type: KClass<T>, block: JsonParser.(DeserializationContext) -> T) {
    this.addDeserializer(type.java, object : StdDeserializer<T>(type.java) {
        override fun deserialize(parser: JsonParser, context: DeserializationContext): T = block(parser, context)
    })
}

fun SimpleModule.keyDeserializer(type: KClass<*>, block: DeserializationContext.(String) -> Any) {
    this.addKeyDeserializer(type.java, object : KeyDeserializer() {
        override fun deserializeKey(key: String, context: DeserializationContext): Any = block(context, key)
    })
}

fun simpleJacksonModule(block: SimpleModule.() -> Unit): SimpleModule {
    return SimpleModule().apply(block)
}
