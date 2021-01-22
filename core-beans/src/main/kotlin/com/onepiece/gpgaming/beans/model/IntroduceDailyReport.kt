package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate

data class IntroduceDailyReport(

        // 业主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 日期
        val day: LocalDate,

        // 注册量
        val registerCount: Int,

        // 首充人数
        val firstDepositCount: Int,

        // 佣金
        val commissions: BigDecimal
) {

    companion object {

        fun empty(clientId: Int, memberId: Int): IntroduceDailyReport {
            return IntroduceDailyReport(clientId = clientId, memberId = memberId, day = LocalDate.now(), registerCount = 0,
                    firstDepositCount = 0, commissions = BigDecimal.ZERO, username = "")
        }

    }

}