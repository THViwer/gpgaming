package com.onepiece.gpgaming.player.controller.value

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
            val totalMWin: BigDecimal,

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

}