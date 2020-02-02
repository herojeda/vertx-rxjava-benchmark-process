package com.ragnaroft.process_benchmarks.configuration.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.json.jackson.DatabindCodec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonMapperConfig {

    companion object {
        fun applyConfig() {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            DatabindCodec.mapper().apply {
                registerModule(
                    JavaTimeModule().addDeserializer(
                        LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter)
                    ).addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
                )

                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                registerModule(KotlinModule())
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            }
        }
    }

}
