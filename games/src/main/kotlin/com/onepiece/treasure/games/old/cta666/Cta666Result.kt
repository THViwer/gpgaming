package com.onepiece.treasure.games.old.cta666

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class Cta666Result {


    data class Register(

            val codeId: Int,

            val token: String,

            val random: String,

            val data: String
    )

    data class StartGame(

            val codeId: Int,

            val token: String,

            val list: List<String>
    )

    data class Balance(

            val codeId: Int,

            val token: String,

            val random: String,

            val member: Member

    )

    data class Member(
            val username: String,

            val balance: BigDecimal

    )

    data class Transfer(
            val codeId: Int,

            val token: String,

            val random: String,

            // 转账流水号
            val data: String,

            val member: Member
    )

    data class Report(

            val codeId: Int,

            val token: String,

            val random: String,

            val list: List<BetDetail>?
    )

    data class BetDetail(

            // 注单唯一Id
            val id: Long,

            // 游戏大厅号 3:现场厅, 4:波贝厅	可以为空
            val lobbyId: Int?,

            // 游戏桌号	可以为空
            val tableId: Int,

            // 游戏靴号	可以为空
            val shoeId: Long?,

            // 游戏局号	可以为空
            val playId: Long,

            // 游戏类型
            val gameType: Int,

            // 游戏Id
            val gameId: Int,

            // 会员Id
            val memberId: Long,

            // TODO 游戏下注时间
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            val betTime: LocalDateTime,

            // TODO 游戏结算时间	可以为空
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            val calTime: LocalDateTime?,

            // 派彩金额 (输赢应扣除下注金额)	可以为空
            val winOrLoss: BigDecimal?,

            // 好路追注派彩金额	winOrLoss为总派彩金额
            val winOrLossz: BigDecimal?,

            // 下注金额
            val betPoints: BigDecimal,

            // 好路追注金额	betPoints为总金额
            val betPointsz: BigDecimal,

            // 有效下注金额
            val availableBet: BigDecimal?,

            // 用户名
            val userName: String,

            // 游戏结果
            val result: String?,

            // 下注注单
            val betDetail: String?,

            // 好路追注注单
            val betDetailz: String?,

            // 下注时客户端IP
            val ip: String,

            // 游戏唯一ID
            val ext: String,

            // 是否结算：0:未结算, 1:已结算, 2:已撤销(该注单为对冲注单)
            val isRevocation: Int,

            // 余额
            val balanceBefore: BigDecimal,

            // 撤销的那比注单的ID	对冲注单才有,可以为空
            val parentBetId: Long?,

            // 货币ID
            val currencyId: Int,

            // 下注时客户端类型
            val deviceType: Int,

            // 追注转账流水号	共享钱包API可用于对账,普通转账API可忽略
            val pluginId: Int

    )

    data class Mark(
            val codeId: Int,

            val token: String,

            val random: String
    )

}