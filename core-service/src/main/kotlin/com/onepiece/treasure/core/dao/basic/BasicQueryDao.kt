package com.onepiece.treasure.core.dao.basic

interface BasicQueryDao<T> {

    fun get(id: Int): T

    fun all(): List<T>

}