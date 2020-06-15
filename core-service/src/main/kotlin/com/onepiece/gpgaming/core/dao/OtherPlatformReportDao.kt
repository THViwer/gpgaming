package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.OtherPlatformReport
import com.onepiece.gpgaming.beans.value.database.OtherPlatformReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface OtherPlatformReportDao : BasicDao<OtherPlatformReport> {

    fun list(startDate: LocalDate): List<OtherPlatformReport>

    fun create(co: OtherPlatformReportValue.PlatformReportCo)

}