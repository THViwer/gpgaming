package com.onepiece.gpgaming.utils

class EmptyRedisService : RedisService {

    override fun put(key: String, value: Any, timeout: Int?) {
    }

    override fun <T> getList(key: String, clz: Class<T>, timeout: Int?, function: () -> List<T>): List<T> {
        return function()
    }

    override fun increase(key: String, timeout: Int?): Long {
        return 1L
    }

    override fun <T> get(key: String, clz: Class<T>, timeout: Int?, function: () -> T?): T? {
        return null
    }

    override fun <T> get(key: String, clz: Class<T>): T? {
        return null
    }

    override fun <T> get(key: String, clz: Class<T>, function: () -> T?): T? {
        return function()
    }

    override fun delete(vararg keys: String) {
    }
}