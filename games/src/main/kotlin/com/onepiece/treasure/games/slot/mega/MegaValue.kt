package com.onepiece.treasure.games.slot.mega

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.math.BigDecimal

sealed class MegaValue {



    data class RegisterResult(

            val id: String,

            val result: Data?,

            val error: String?,

            val jsonrpc: String

    ) {
        data class Data(
                val success: Boolean,

                val userId: Long,

                val nickname: String,

                val regType: String,

                val loginId: String
        )
    }

    data class BalanceResult(

            val id: String,

            val result: BigDecimal,

            val error: String?,

            val jsonrpc: String

    )



    data class DownAppResult(
            val id: String,

            val result: String,

            val error: String?,

            val jsonrpc: String
    )


    data class BetQueryResult(
            val id: String,

            val result: String,

            val error: String?,

            val jsonrpc: String
    )


}

fun main() {
    val json = "{\"id\":\"a3892e53-1361-498b-9f31-255c812871b9\",\"result\":100,\"error\":null,\"jsonrpc\":\"2.0\"}"
    val x = jacksonObjectMapper().readValue<MegaValue.BalanceResult>(json)
    println(x)
}