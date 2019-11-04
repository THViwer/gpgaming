package com.onepiece.treasure.core.order

import java.sql.PreparedStatement
import java.time.LocalDate

interface BetOrderDao<T> {

    fun create(orders: List<T>)

    fun query(query: BetOrderValue.Query): List<T>

    fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report>
}

fun PreparedStatement.setIntOrNull(index: Int, v: Int?) {
    if (v == null) this.setNull(index, java.sql.Types.INTEGER) else this.setInt(index, v)
}

fun PreparedStatement.setLongOrNull(index: Int, v: Long?) {
    if (v == null) this.setNull(index, java.sql.Types.BIGINT) else this.setLong(index, v)
}