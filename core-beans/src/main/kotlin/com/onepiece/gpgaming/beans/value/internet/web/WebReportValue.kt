package com.onepiece.gpgaming.beans.value.internet.web

import com.alibaba.excel.annotation.ExcelIgnore
import com.alibaba.excel.annotation.ExcelProperty
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
        @ExcelProperty("日期")
        val day: String,

        // 厅主Id
        @ExcelProperty("业主id")
        val clientId: Int,

        // 会员Id
        @ExcelProperty("会员Id")
        val memberId: Int,

        @ExcelProperty("手机号")
        val phone: String,

        // 用户名
        @ExcelProperty("用户名")
        val username :String,

        // 转入金额
        @ExcelProperty("转入金额")
        val transferIn: BigDecimal,

        // 转出金额
        @ExcelProperty("转出金额")
        val transferOut: BigDecimal,

        // 充值金额
        @ExcelProperty("银行转账金额")
        val depositAmount: BigDecimal,

        // 第三方充值金额
        @ExcelProperty("三方充值金额")
        val thirdPayAmount: BigDecimal,

        // 第三方充值总数
        @ExcelProperty("三方充值次数")
        val thirdPayCount: Int,

        // 取款金额
        @ExcelProperty("取款金额")
        val withdrawAmount: BigDecimal,

        // 人工提存金额
        @ExcelProperty("人工提存金额")
        val artificialAmount: BigDecimal,

        // 人工提存次数
        @ExcelProperty("人工提存次数")
        val artificialCount: Int,

        // 下注金额
        @ExcelProperty("总下注")
        val totalBet: BigDecimal,

        @ExcelProperty("下注次数")
        val betCount: Int,

        // 盈利金额
        @ExcelProperty("总支出")
        val payout: BigDecimal,

        // 返水金额
        @ExcelProperty("返水金额")
        val rebateAmount: BigDecimal,

        //  优惠金额
        @ExcelProperty("优惠金额")
        val promotionAmount: BigDecimal,

        // 平台结算列表
        @ExcelIgnore
        val settles: List<MemberDailyReport.PlatformSettle>

) {

        @ExcelIgnore
        val totalMWin: BigDecimal = this.payout.minus(this.totalBet)

    // 业主盈利
//    val totalCWin: BigDecimal
//        get() {
//            return totalBet.minus(totalMWin)
//        }

}