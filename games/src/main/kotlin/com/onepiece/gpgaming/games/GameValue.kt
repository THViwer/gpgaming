package com.onepiece.gpgaming.games

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.ClientToken
import java.math.BigDecimal
import java.time.LocalDate
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

            val launch: LaunchMethod,

            val redirectUrl: String
    )

    data class StartSlotReq(

            val token: ClientToken,

            val gameId: String,

            val username: String,

            val password: String,

            val language: Language,

            val launchMethod: LaunchMethod,

            // 重定向地址
            val redirectUrl: String

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

            val password: String,

            val amount: BigDecimal
    )

    data class TransferResp(
            // 是否转账成功
            val transfer: Boolean,

            // 平台订单Id
            val platformOrderId: String,

            // 转账后余额
            val balance: BigDecimal,

            // 转账错误信息
            val msg: String = ""
    ) {

        companion object {

            fun of(successful: Boolean, balance: BigDecimal = BigDecimal.valueOf(-1), platformOrderId: String = "-"): TransferResp {
                return TransferResp(transfer = successful, balance = balance, platformOrderId = platformOrderId)
            }

            fun successful(balance: BigDecimal = BigDecimal.valueOf(-1), platformOrderId: String = "-"): TransferResp {
                return TransferResp(transfer = true, platformOrderId = platformOrderId, balance = balance)
            }

            fun failed(): TransferResp {
                return TransferResp(transfer = false, platformOrderId = "-", balance = BigDecimal.valueOf(-1))
            }
        }
    }

    data class PlatformReportData(

            // 用户名
            val username: String,

            // 平台
            val platform: Platform,

            // 下注金额
            val bet: BigDecimal,

            // 盈利
            val win: BigDecimal,

            // 原始数据
            val originData: String
    )

    data class CheckTransferReq(
            val token: ClientToken,

            val username: String,

            val orderId: String,

            val amount: BigDecimal,


            // deposit or withdraw
            val type: String,

            val platformOrderId: String
    )

    data class UpdatePasswordReq(
            val token: ClientToken,

            val username: String,

            val password: String
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

            val platform: Platform,

            val startTime: LocalDateTime,

            val endTime: LocalDateTime
    )

    data class ReportQueryReq(

            // 用户名
            val token: ClientToken,

            // 开始日期
            val startDate: LocalDate
    )


}