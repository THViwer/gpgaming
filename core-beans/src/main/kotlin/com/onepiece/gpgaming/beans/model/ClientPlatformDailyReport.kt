package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

data class ClientPlatformDailyReport(

        // id
//        val id: Int,

        // 报表日期
        val day: String,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 下注金额
        val bet: BigDecimal,

        // 派彩
        val payout: BigDecimal,

        // 转入金额
        val transferIn: BigDecimal,

        // 转出金额
        val transferOut: BigDecimal,

        // 优惠金额
        val promotionAmount: BigDecimal,

        // 返水金额
//        val rebateAmount: BigDecimal,

        // 存活人数(有金额转入平台)
        val activeCount: Int,

        // 创建时间
        val createdTime: LocalDateTime,

        // 状态
        val status: Status

) {

    val win = this.payout.minus(this.bet)

//    val clientWin: BigDecimal
//        get() {
//            return bet.minus(win)
//        }

}