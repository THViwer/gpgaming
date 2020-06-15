package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate

sealed class OtherPlatformReportValue {

    data class PlatformReportCo(

            // bossId
            val bossId: Int,

            // clientId
            val clientId: Int,

            // 会员Id
            val memberId: Int,

            // 日期
            val day: LocalDate,

            // 平台
            val platform: Platform,

            // 下注金额
            val bet: BigDecimal,

            // 盈利
            val win: BigDecimal,

            // 原始数据
            val originData: String
    )

}