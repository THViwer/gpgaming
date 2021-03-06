package com.onepiece.gpgaming.task.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.onepiece.gpgaming.utils.DefaultRedisService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
open class RedisConfig {

    @Bean
    @Primary
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    open fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
                .registerModule(ParameterNamesModule())
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        return objectMapper

    }

    @Bean
    open fun xmlMapper(): XmlMapper {
        val xmlMapper = XmlMapper()
        xmlMapper.registerModule(KotlinModule())
        return xmlMapper
    }


    @Primary
    @Bean
    open fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, String> {

        val redisTemplate = RedisTemplate<String, String>()
        //??????StringRedisTemplate??????????????????????????????
        redisTemplate.connectionFactory = factory
        redisTemplate.keySerializer = keySerializer()
        redisTemplate.hashKeySerializer = keySerializer()
        redisTemplate.valueSerializer = valueSerializer()
        redisTemplate.hashValueSerializer = valueSerializer()

        return redisTemplate

    }

    @Bean
    open fun redisService(
            redisTemplate: RedisTemplate<String, String>,
            objectMapper: ObjectMapper
    ): RedisService {
        return DefaultRedisService(redisTemplate, objectMapper)
    }


    @Bean
    open fun tokenRedisTemplate(factory: LettuceConnectionFactory): RedisTemplate<String, Any> {

        //        factory.setDatabase(1);

        val redisTemplate = RedisTemplate<String, Any>()
        //??????StringRedisTemplate??????????????????????????????
        redisTemplate.connectionFactory = factory
        redisTemplate.keySerializer = keySerializer()
        redisTemplate.hashKeySerializer = keySerializer()
        redisTemplate.valueSerializer = JdkSerializationRedisSerializer()
        redisTemplate.hashValueSerializer = valueSerializer()

        return redisTemplate
    }

    private fun keySerializer(): RedisSerializer<String> {
        return StringRedisSerializer()
    }

    //??????Jackson????????????
    private fun valueSerializer(): RedisSerializer<String> {
        return StringRedisSerializer()
    }

}
