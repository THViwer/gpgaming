package com.onepiece.treasure.games.joker

import java.util.*
import kotlin.collections.ArrayList

object JokerConstant {

    val url = "http://www.joker.com"

    val appId = "app1"

    val Signature = "=MDAwMDAwMDA%3D"

}

class JokerParamBuilder private constructor(
        val method: String
){

     val data: ArrayList<String> = arrayListOf()

    companion object {
        fun instance(method: String): JokerParamBuilder {
            return JokerParamBuilder(method)
        }
    }

    fun set(k: String, v: String): JokerParamBuilder {
        data.add("$k=$v")
        return this
    }

    private fun sign(): String {

        return UUID.randomUUID().toString()
    }

    fun build(): String {
        val urlParam = data.joinToString(separator = "&")
        return "AppID=${JokerConstant.appId}&Signature=${sign()}&Method=$method&Timestamp=${System.currentTimeMillis()/1000}&$urlParam"
    }

}