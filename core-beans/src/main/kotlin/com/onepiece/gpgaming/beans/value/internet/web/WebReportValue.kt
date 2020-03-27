package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
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
        val depositMoney: BigDecimal,

        // 取款金额
        val withdrawMoney: BigDecimal,

        // 人工提存金额
        val artificialMoney: BigDecimal,

        // 人工提存次数
        val artificialCount: Int

)