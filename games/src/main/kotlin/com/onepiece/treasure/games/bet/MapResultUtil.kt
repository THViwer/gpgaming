package com.onepiece.treasure.games.bet

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val DEFAULT_DATETIMEFORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
object MapResultUtil {

    fun asString(map: Map<String, Any>, key: String): String {
        return map[key]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asInt(map: Map<String, Any>, key: String): Int {
        return map[key]?.toString()?.toInt()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asBigDecimal(map: Map<String, Any>, key: String): BigDecimal {
        return map[key]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asMap(map: Map<String, Any>, key: String): Map<String, Any> {
        return map[key]?.let { it as Map<String, Any> }?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asLocalDateTime(map: Map<String, Any>, key: String, dateTimeFormatter: DateTimeFormatter = DEFAULT_DATETIMEFORMATTER): LocalDateTime {
        return map[key]?.let { LocalDateTime.parse(it.toString(), dateTimeFormatter) }?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asLocalDateTime(map: Map<String, Any>, key: String): LocalDateTime {

        return map[key]?.toString()?.substring(0, 19)?.let { LocalDateTime.parse(it) } ?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    fun asList(map: Map<String, Any>, key: String): List<Map<String, Any>> {
        return map[key]?.let { it as List<Map<String, Any>> }?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }
}