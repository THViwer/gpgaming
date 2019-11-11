package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.model.token.ClientToken
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class GameValue {

    data class RegisterReq(
            val token: ClientToken,

            val name: String,

            val username: String,

            val password: String
    )

    data class StartReq(

            val token: ClientToken,

            val username: String,

            val startPlatform: StartPlatform
    )

    data class StartSlotReq(

            val token: ClientToken,

            val gameId: String,

            val username: String
    )

    data class BalanceReq(
            val token: ClientToken,

            val username: String
    )

    data class TransferReq(
            val token: ClientToken,

            val orderId: String,

            val username: String,

            val amount: BigDecimal

    )

    data class CheckTransferReq(
            val token: ClientToken,

            val username: String,

            val orderId: String
    )

    data class BetOrderReq(
            val token: ClientToken,

            val username: String,

            val startTime: LocalDateTime,

            val endTIme: LocalDateTime
    )

    data class SyncBetOrderReq(
            val token: ClientToken,

            val startTime: LocalDateTime,

            val endTime: LocalDateTime
    )

}