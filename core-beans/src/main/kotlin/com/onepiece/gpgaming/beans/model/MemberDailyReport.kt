package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class MemberDailyReport(

        val id: Int,

        // 日期
        val day: LocalDate,

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 上级代理
        val superiorAgentId: Int,

        // 代理
        val agentId: Int,

        // 电销人员Id
        val saleId: Int,

        // 营销Id
        val marketId: Int,

        // 电销类型
        val saleScope: SaleScope,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

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
        val depositAmount: BigDecimal,

        // 取款次数
        val withdrawCount: Int,

        // 人工提存金额
        val artificialAmount: BigDecimal,

        // 人工提存次数
        val artificialCount: Int,

        // 自动入款金额
        val thirdPayAmount: BigDecimal,

        // 自动入款次数
        val thirdPayCount: Int,

        // 取款金额
        val withdrawAmount: BigDecimal,
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

        //  优惠金额
        val promotionAmount: BigDecimal,

        // 返水金额
        val rebateAmount: BigDecimal,

        // 反水金额是否已进行
        val rebateExecution: Boolean,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

) {

    //会员层级
    var levelId: Int = 0

    //优惠必要打码
    var slotRequirementBet: BigDecimal = BigDecimal.ZERO

    // 真人必要打码
    var liveRequirementBet: BigDecimal = BigDecimal.ZERO

    // 体育必要打码
    var sportRequirementBet: BigDecimal = BigDecimal.ZERO

    // 捕鱼必要打码
    var fishRequirementBet: BigDecimal = BigDecimal.ZERO

    fun expand(levelId: Int, slotRequirementBet: BigDecimal, liveRequirementBet: BigDecimal, sportRequirementBet: BigDecimal, fishRequirementBet: BigDecimal): MemberDailyReport {
        this.levelId = levelId
        this.slotRequirementBet = slotRequirementBet
        this.liveRequirementBet = liveRequirementBet
        this.sportRequirementBet = sportRequirementBet
        this.fishRequirementBet = fishRequirementBet

        return this
    }

    fun isHasData(): Boolean {
        return depositCount > 0 || thirdPayCount > 0 || withdrawCount > 0 || rebateAmount.toDouble() > 0 && promotionAmount.toDouble() > 0 || totalBet.toDouble() > 0
                || settles.isNotEmpty()
    }


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
            val mwin: BigDecimal = BigDecimal.ZERO,

            // 反水
            val rebate: BigDecimal = BigDecimal.ZERO
    ) {

        // 业主盈利
        val cwin: BigDecimal
            get() {
                return bet.minus(mwin)
            }

    }

}