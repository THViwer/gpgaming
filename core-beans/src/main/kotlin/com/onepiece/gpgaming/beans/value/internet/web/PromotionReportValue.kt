package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

sealed class PromotionReportValue {

    data class PromotionReportVo(
            // 厅主Id
            val clientId: Int,

            // 日期
            val day: LocalDate,

            // 优惠Id
            val promotionId: Int,

            // 优惠平台
            val promotionPlatforms: String,

            // 优惠金额
            val promotionAmount: BigDecimal,

            // 创建时间
            val createdTime: LocalDateTime,

            // 状态
            val status: Status
    )

}