package com.onepiece.gpgaming.beans.value.internet.web

import com.alibaba.excel.annotation.ExcelProperty
import java.math.BigDecimal

data class ClientReportExcelVo(

        @ExcelProperty("日期")
        val day: String,

        @ExcelProperty("下注金额")
        val totalBet: BigDecimal,

        @ExcelProperty("派彩金额")
        val payout: BigDecimal,

        @ExcelProperty("转入金额")
        val transferIn: BigDecimal,

        @ExcelProperty("转出金额")
        val transferOut: BigDecimal,

        @ExcelProperty("充值金额")
        val depositAmount: BigDecimal,

        @ExcelProperty("充值次数")
        val depositCount: Int,

//        @ExcelProperty("充值人数")
//        val depositSequence: Int,

        @ExcelProperty("优惠金额")
        val promotionAmount: BigDecimal,

        @ExcelProperty("取款金额")
        val withdrawAmount: BigDecimal,

        @ExcelProperty("取款次数")
        val withdrawCount: Int,

        @ExcelProperty("人工提存金锭")
        val artificialAmount: BigDecimal,

        @ExcelProperty("人工提存次数")
        val artificialCount: Int,

        @ExcelProperty("第三方充值金额")
        val thirdPayAmount: BigDecimal,

        @ExcelProperty("第三方充值总数")
        val thirdPayCount: Int,

//        @ExcelProperty("三方充值人数")
//        val thirdPaySequence: Int,

        @ExcelProperty("返水金额")
        val rebateAmount: BigDecimal,

        @ExcelProperty("今日新增用户")
        val newMemberCount: Int

)
