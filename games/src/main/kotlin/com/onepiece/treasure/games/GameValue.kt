package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.model.token.ClientToken
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class GameValue {

    data class RegisterReq(

            val clientId: Int,

            val memberId: Int,

            val token: ClientToken,

            val name: String,

            val username: String,

            val password: String
    )

    data class StartReq(

            val token: ClientToken,

            val username: String,

            val password: String,

            val language: Language,

            val startPlatform: LaunchMethod,

            val redirectUrl: String = "http://www.baidu.com"
    )

    data class StartSlotReq(

            val token: ClientToken,

            val gameId: String,

            val username: String,

            val language: Language,

            val launchMethod: LaunchMethod,

            // 重定向地址
            val redirectUrl: String = "http://www.baidu.com"

    )

    data class BalanceReq(
            val token: ClientToken,

            val username: String,

            val password: String
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

            val orderId: String,

            val platformOrderId: String
    )

    data class BetOrderReq(
            val token: ClientToken,

            val username: String,

            val startTime: LocalDateTime,

            val endTime: LocalDateTime
    )

    data class SyncBetOrderReq(

            val token: ClientToken,

            val startTime: LocalDateTime,

            val endTime: LocalDateTime
    )

    data class PullBetOrderReq(

            val clientId: Int,

            val token: ClientToken,

            val startTime: LocalDateTime,

            val endTime: LocalDateTime
    )


}