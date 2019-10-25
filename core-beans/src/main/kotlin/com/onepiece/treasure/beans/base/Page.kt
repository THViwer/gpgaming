package com.onepiece.treasure.beans.base

open class Page<T> private constructor(

        val data: List<T>,

        val total: Int

) {

    companion object {

        fun <T> of(total: Int, data: List<T>): Page<T> {
            return Page(total = total, data = data)
        }

        fun <T> empty(): Page<T> {
            return Page(total = 0, data = emptyList())
        }

    }

}