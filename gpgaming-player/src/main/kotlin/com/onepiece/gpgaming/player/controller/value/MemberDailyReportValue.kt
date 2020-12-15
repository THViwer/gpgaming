package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import java.math.BigDecimal
import java.time.LocalDate

sealed class MemberDailyReportValue {

    data class ReportVo(

            val memberId: Int,

            val day: LocalDate,

            // 平台结算列表
            val settles: List<MemberDailyReport.PlatformSettle>,

            // 顾客盈利
            val payout: BigDecimal,

            // 顾客下注
            val totalBet: BigDecimal,

            // 充值金额(转账+自动入款金额)
            val depositAmount: BigDecimal,

            // 取款金额
            val withdrawAmount: BigDecimal,

            //  优惠金额
            val promotionAmount: BigDecimal,

            // 返水金额
            val rebateAmount: BigDecimal,

            // 反水金额是否已进行
            val rebateExecution: Boolean
    )

    // 返水计算方式：(validBet - requirementBet) * rebateScale
    data class PlatformSettleVo(

            val day: LocalDate,

            val platform: Platform,

            // 下注
            val bet: BigDecimal = BigDecimal.ZERO,

            // 有效投注
            val validBet: BigDecimal = BigDecimal.ZERO,

            // 派彩 应该改为payout
            val payout: BigDecimal = BigDecimal.ZERO,

            // 反水
            val rebate: BigDecimal = BigDecimal.ZERO,


            // 必要打码
            val requirementBet: BigDecimal = BigDecimal.ZERO,

            // 反水比例
            val rebateScale: BigDecimal = BigDecimal.ZERO



    )

}