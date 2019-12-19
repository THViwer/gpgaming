package com.onepiece.gpgaming.core.dao.basic

import java.sql.ResultSet

interface BasicDao<T> {

    fun get(id: Int): T

    fun all(clientId: Int): List<T>

    fun all(): List<T>

}


fun ResultSet.getIntOrNull(column: String): Int? {
    return this.getObject(column)?.toString()?.toIntOrNull()
}