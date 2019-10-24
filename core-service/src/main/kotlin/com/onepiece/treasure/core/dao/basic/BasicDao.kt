package com.onepiece.treasure.core.dao.basic

interface BasicDao<T> {

    fun get(id: Int): T

    fun all(clientId: Int): List<T>

    fun all(): List<T>

}