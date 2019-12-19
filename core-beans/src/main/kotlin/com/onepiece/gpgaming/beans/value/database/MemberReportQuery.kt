package com.onepiece.gpgaming.beans.value.database

import java.time.LocalDate

data class MemberReportQuery(

        val clientId: Int,

        val memberId: Int?,

        val startDate: LocalDate,

        val endDate: LocalDate
)