package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import java.math.BigDecimal
import java.time.LocalDate

class MemberPlatformReportWebVo(

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 平台Id
        val platform: Platform,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal

)


data class MemberReportWebVo(

        // 日期
        val day: LocalDate,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username :String,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 充值金额
        val depositAmount: BigDecimal,

        // 第三方充值金额
        val thirdPayAmount: BigDecimal,

        // 第三方充值总数
        val thirdPayCount: Int,

        // 取款金额
        val withdrawAmount: BigDecimal,

        // 人工提存金额
        val artificialAmount: BigDecimal,

        // 人工提存次数
        val artificialCount: Int,

        // 下注金额
        val totalBet: BigDecimal,

        // 盈利金额
        val payout: BigDecimal,

        // 返水金额
        val rebateAmount: BigDecimal,

        //  优惠金额
        val promotionAmount: BigDecimal,

        // 平台结算列表
        val settles: List<MemberDailyReport.PlatformSettle>

) {

    val totalMWin: BigDecimal = this.payout.minus(this.totalBet)

    // 业主盈利
//    val totalCWin: BigDecimal
//        get() {
//            return totalBet.minus(totalMWin)
//        }

}