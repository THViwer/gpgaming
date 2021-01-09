package com.onepiece.gpgaming.web.controller.value

import com.alibaba.excel.annotation.ExcelProperty
import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDateTime


data class BetOrderExcel(

        @ExcelProperty("会员id")
        val memberId: Int,

        @ExcelProperty("订单Id")
        val orderId: String,

        @ExcelProperty("平台")
        val platform: Platform,

        @ExcelProperty("下注金额")
        val betAmount: BigDecimal,

        @ExcelProperty("有效投注金额")
        val validAmount: BigDecimal,

        @ExcelProperty("获得金额")
        val payout: BigDecimal,

        @ExcelProperty("标记已处理打码量")
        val mark: Boolean,

//        @ExcelProperty("原始订单数据(json格式)")
//        val originData: String,

        @ExcelProperty("下注时间")
        val betTime: LocalDateTime,

        @ExcelProperty("结算时间")
        val settleTime: LocalDateTime,

        @ExcelProperty("创建时间")
        val createdTime: LocalDateTime

)