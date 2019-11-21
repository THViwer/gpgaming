package com.onepiece.treasure.games.bet

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MapUtil private constructor(
        val data: Map<String, Any>
) {

    companion object {

        fun instance(data: Map<String, Any>): MapUtil {
            return MapUtil(data)
        }
    }

    fun asString(key: String): String {
        return MapResultUtil.asString(map = data, key = key)
    }

    fun asInt(key: String): Int {
        return MapResultUtil.asInt(map = data, key = key)
    }

    fun asBigDecimal(key: String): BigDecimal {
        return MapResultUtil.asBigDecimal(map = data, key = key)
    }

    fun asMap(key: String): MapUtil {
        val m = MapResultUtil.asMap(map = data, key = key)
        return MapUtil(m)
    }

    fun asLocalDateTime(key: String, dateTimeFormatter: DateTimeFormatter = DEFAULT_DATETIMEFORMATTER): LocalDateTime {
        return MapResultUtil.asLocalDateTime(map = data, key = key, dateTimeFormatter = dateTimeFormatter)
    }

    fun asLocalDateTime(key: String): LocalDateTime {

        return MapResultUtil.asLocalDateTime(map = data, key = key)
    }

    fun asList(key: String): List<MapUtil> {
        return MapResultUtil.asList(map = data, key = key).map { instance(it) }
    }

}