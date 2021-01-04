package com.onepiece.gpgaming.core.daily

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import java.math.BigDecimal


/**
 * 在用户列表里详情里加个功能加个日期 查询顾客 在制定日期里 的数据，应该要有的数据。充值金额，充值次数，提款金额，提款次数，领取优惠金额，领取优惠次数

平台， 转入多少进平台，转出多少进平台，总下注，盈利，优惠金额，返水金额


 */
data class MemberDailyDetail(

        // 会员Id
        val memberId: Int,

        // 充值金额
        val totalDeposit: BigDecimal = BigDecimal.ZERO,

        // 充值次数
        val totalDepositFrequency: Int = 0,

        // 提款金额
        val totalWithdraw: BigDecimal = BigDecimal.ZERO,

        // 提款次数
        val totalWithdrawFrequency: Int = 0,

        // 优惠金额
        val totalPromotion: BigDecimal = BigDecimal.ZERO,

        // 优惠次数
//        val totalPromotionFrequency: Int = 0,

        // 平台结算列表
        val settles: List<MemberDailyReport.PlatformSettle> = emptyList()
) {


    // 总下注
    val totalBet: BigDecimal
        get() {
            if (settles.isEmpty()) return BigDecimal.ZERO
            return settles.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2)
        }

    // 顾客盈利
    val payout: BigDecimal
        get() {
            if (settles.isEmpty()) return BigDecimal.ZERO
            return settles.sumByDouble { it.payout.toDouble() }.toBigDecimal().setScale(2, 2)
        }

}