package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.OtherPlatformReport
import com.onepiece.gpgaming.beans.value.database.OtherPlatformReportValue
import com.onepiece.gpgaming.core.dao.OtherPlatformReportDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate


@Repository
class OtherPlatformReportDaoImpl : BasicDaoImpl<OtherPlatformReport>("other_platform_report"), OtherPlatformReportDao {

    override val mapper: (rs: ResultSet) -> OtherPlatformReport
        get() = TODO("Not yet implemented")

    override fun list(startDate: LocalDate): List<OtherPlatformReport> {
        TODO("Not yet implemented")
    }

    override fun create(co: OtherPlatformReportValue.PlatformReportCo) {
        TODO("Not yet implemented")
    }
}