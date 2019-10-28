package com.onepiece.treasure.games.joker.value

interface JokerValue {

    val appId: String
        get() {
            return "app1"
        }

    val timestamp: Int
        get()  {
            return (System.currentTimeMillis() / 1000).toInt()
        }

    val signature: String
        get() {
            return "Signature=hello"
        }

    fun toReqParam(): String
}