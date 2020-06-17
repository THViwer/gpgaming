package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class OtherPlatformReport (

        // id
        val id: Int,

        // bossId
//        val bossId: Int,

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
        val originData: String,

        // 创建时间
        val createdTime: LocalDateTime

)