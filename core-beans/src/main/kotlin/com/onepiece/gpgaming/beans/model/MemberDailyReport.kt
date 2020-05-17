package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberDailyReport(

        val id: Int,

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 代理
        val agentId: Int,

        // 会员Id
        val memberId: Int,

        // 平台结算列表
        val settles: List<PlatformSettle>,

        // 顾客盈利
        val totalMWin: BigDecimal,

        // 顾客下注
        val totalBet: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 存款次数
        val depositCount: Int,

        // 充值金额
        val depositMoney: BigDecimal,

        // 取款次数
        val withdrawCount: Int,

        // 人工提存金额
        val artificialMoney: BigDecimal,

        // 人工提存次数
        val artificialCount: Int,

        // 自动入款金额
        val thirdPayMoney: BigDecimal,

        // 自动入款次数
        val thirdPayCount: Int,

        // 取款金额
        val withdrawMoney: BigDecimal,
//
//        // 老虎机返水金额
//        val slotRebate: BigDecimal,
//
//        // 捕鱼返水金额
//        val flshRebate: BigDecimal,
//
//        // 真人返水金额
//        val liveRebate: BigDecimal,
//
//        // 体育返水金额
//        val sportRebate: BigDecimal,

        // 返水金额
        val backwaterMoney: BigDecimal,

        //  优惠金额
        val promotionMoney: BigDecimal,

        // 反水金额是否已进行
        val backwaterExecution: Boolean,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

) {

    // 下注金额
//    val totalBet: BigDecimal
//        get() {
//            return settles.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2)
//        }

    // 金额  正数: 会员赢钱、厅主输钱 负数：会员输钱、厅主赢钱
//    val totalMWin: BigDecimal
//        get() {
//            return settles.sumByDouble { it.cwin.toDouble() }.toBigDecimal().setScale(2, 2)
//        }

    // 业主盈利金额
    val totalCWin = totalBet.minus(totalMWin)

    data class PlatformSettle(

            // 平台
            val platform: Platform,

            // 下注
            val bet: BigDecimal = BigDecimal.ZERO,

            // 有效投注
            val validBet: BigDecimal = BigDecimal.ZERO,

            // 顾客盈利
            val mwin: BigDecimal = BigDecimal.ZERO
    ) {

        // 业主盈利
        val cwin: BigDecimal
            get() {
                return bet.minus(mwin)
            }

    }

}