package com.onepiece.gpgaming.beans.value.database

import java.math.BigDecimal
import java.time.LocalDate

data class MemberReportQuery(

        val clientId: Int,

        val agentId: Int?,

        val memberId: Int?,

        val startDate: LocalDate,

        val endDate: LocalDate,

        val minRebateAmount: BigDecimal?,

        val minPromotionAmount: BigDecimal?,

        val current: Int,

        val size: Int

)