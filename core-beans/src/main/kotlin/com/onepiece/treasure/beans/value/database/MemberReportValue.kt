package com.onepiece.treasure.beans.value.database

import java.time.LocalDate

data class MemberReportQuery(

        val memberId: Int,

        val startDate: LocalDate,

        val endDate: LocalDate
)