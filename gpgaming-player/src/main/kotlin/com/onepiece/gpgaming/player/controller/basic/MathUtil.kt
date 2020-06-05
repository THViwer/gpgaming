package com.onepiece.gpgaming.player.controller.basic

import kotlin.random.Random

object MathUtil {

    fun <T> getRandom(list: List<T>?) : T? {
        return list?.let { list[Random.nextInt(list.size)] }
    }

}