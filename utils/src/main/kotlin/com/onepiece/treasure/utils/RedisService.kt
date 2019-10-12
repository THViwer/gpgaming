package com.onepiece.treasure.utils

interface RedisService {

    fun put(key: String, value: Any, timeout: Int? = null)

    fun <T> getList(key: String, clz: Class<T>, timeout: Int? = null, function: () -> List<T>): List<T>

    fun increase(key: String, timeout: Int? = null): Long

    fun <T> get(key: String, clz: Class<T>, timeout: Int?, function: () -> T?): T?

    fun <T> get(key: String, clz: Class<T>): T?

    fun <T> get(key: String, clz: Class<T>, function: () -> T?): T?

    fun delete(vararg keys: String)

}
