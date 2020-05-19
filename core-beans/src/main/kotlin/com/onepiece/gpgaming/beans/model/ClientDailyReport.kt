package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class ClientDailyReport(
        // id
        val id: Int,

        // 报表时间
        val day: LocalDate,

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 下注金额
        val totalBet: BigDecimal,

        // 盈利金额
        val totalMWin: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 充值金额
        val depositAmount: BigDecimal,

        // 充值次数
        val depositCount: Int,

        // 充值人数
//        val depositSequence: Int,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 取款金额
        val withdrawAmount: BigDecimal,

        // 取款次数
        val withdrawCount: Int,

        // 人工提存金锭
        val artificialAmount: BigDecimal,

        // 人工提存次数
        val artificialCount: Int,

        // 第三方充值金额
        val thirdPayAmount: BigDecimal,

        // 第三方充值总数
        val thirdPayCount: Int,

        // 三方充值人数
//        val thirdPaySequence: Int,

        // 返水金额
        val rebateAmount: BigDecimal,

        // 今日新增用户
        val newMemberCount: Int,

        // 创建时间
        val createdTime: LocalDateTime

) {

    // 业主盈利金额
    val totalCWin: BigDecimal = totalBet.minus(totalMWin)

}