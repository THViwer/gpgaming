package com.onepiece.treasure.games.old.joker.value

interface JokerValue {

    val appId: String
        get() {
            return "F1S8"
        }

    val timestamp: Int
        get()  {
            return (System.currentTimeMillis() / 1000).toInt()
        }

    val signature: String
        get() {
            // Base64 (HMAC_SHA1 (“Method=CU&Timestamp=1447061919&Username=tester”,secret_key))

            return "Signature=hello"
        }

    fun toReqParam(): String
}