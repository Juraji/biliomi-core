package nl.juraji.biliomi.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import nl.juraji.biliomi.domain.EntityId
import nl.juraji.biliomi.utils.serialization.keySerializer
import nl.juraji.biliomi.utils.serialization.serializer
import nl.juraji.biliomi.utils.serialization.simpleJacksonModule
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.*
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@ComponentScan
@Configuration
class SerializationConfiguration {

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val entityIdModule = simpleJacksonModule {
            serializer(EntityId::class) { value, _ -> writeString(value.identifier) }
            keySerializer(EntityId::class) { value, _ -> writeFieldName(value.identifier) }

//                                deserializer(PictureId::class) { PictureId(valueAsString) }
//                                keyDeserializer(PictureId::class) { value -> PictureId(value) }
        }

        return Jackson2ObjectMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .modules(
                        JavaTimeModule(),
                        KotlinModule(),
                        entityIdModule
                )
                .featuresToEnable(
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
                        SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
                        DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
                )
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                )
    }

    @Bean
    @Primary
    fun serializer(objectMapperBuilder: Jackson2ObjectMapperBuilder): Serializer =
            JacksonSerializer.builder()
                    .objectMapper(objectMapperBuilder.build())
                    .build()
}
