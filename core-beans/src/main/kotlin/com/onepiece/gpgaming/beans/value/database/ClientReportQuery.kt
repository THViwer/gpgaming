package com.onepiece.gpgaming.beans.value.database

import java.time.LocalDate

data class ClientReportQuery(

        val clientId: Int,

        val startDate: LocalDate,

        val endDate: LocalDate

)