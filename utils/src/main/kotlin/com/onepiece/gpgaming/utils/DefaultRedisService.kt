package com.onepiece.gpgaming.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

class DefaultRedisService(
        private val redisTemplate: RedisTemplate<String, String>,
        private val objectMapper: ObjectMapper
): RedisService {

    override fun put(key: String, value: Any, timeout: Int?) {
        val data = objectMapper.writeValueAsString(value)

        if (timeout != null) {
            redisTemplate.opsForValue().set(key, data, timeout.toLong(), TimeUnit.SECONDS)
        } else {
            redisTemplate.opsForValue().set(key, data)
        }
    }

    override fun <T> getList(key: String, clz: Class<T>, timeout: Int?, function: () -> List<T>): List<T> {

        val json = redisTemplate.opsForValue().get(key)

        return if (json == null) {
            val data = function()
            if (data.isNotEmpty()) this.put(key, data, timeout)
            data
        } else {
            val javaType = objectMapper.typeFactory.constructCollectionType(List::class.java, clz)
            objectMapper.readValue(json, javaType)
        }

    }

    override fun increase(key: String, timeout: Int?): Long {
        val sequence = redisTemplate.opsForValue().increment(key)!!
        if (sequence == 1L && timeout != null) {
            redisTemplate.expire(key, timeout.toLong(), TimeUnit.SECONDS)
        }

        return sequence

    }

    override fun <T> get(key: String, clz: Class<T>, timeout: Int?, function: () -> T?): T? {
        val json = redisTemplate.opsForValue().get(key)
        return if (json == null) {
            val data = function()
            if (data != null) {
                this.put(key = key, value = data as Any, timeout = timeout)
            }
            data
        } else {
            objectMapper.readValue(json, clz)

        }
    }

    override fun <T> get(key: String, clz: Class<T>): T? {
        val json = redisTemplate.opsForValue().get(key) ?: return null
        return objectMapper.readValue(json, clz)
    }

    override fun <T> get(key: String, clz: Class<T>, function: () -> T?): T? {
        return this.get(key = key, clz = clz, timeout = null, function = function)
    }

    override  fun delete(vararg keys: String) {
        redisTemplate.delete(keys.toList())

    }

    override fun lock(key: String, error: (() -> Unit)?, function: () -> Unit) {
        val flag = redisTemplate.opsForValue().getAndSet(key, "true") == "true"

        try {
            redisTemplate.expire(key, 60, TimeUnit.MINUTES)

            if (flag) {
                function()
            } else {
                if (error != null) error()
            }
        } finally {
            redisTemplate.opsForValue().set(key, "false")
        }
    }
}
