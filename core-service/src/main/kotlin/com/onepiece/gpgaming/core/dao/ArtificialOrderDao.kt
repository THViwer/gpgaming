package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialCReportVo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery
import com.onepiece.gpgaming.beans.value.database.ArtificialReportVo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface ArtificialOrderDao: BasicDao<ArtificialOrder> {

    fun query(query: ArtificialOrderQuery): List<ArtificialOrder>

    fun total(query: ArtificialOrderQuery): Int

    fun create(artificialOrder: ArtificialOrderCo): Boolean

    fun mReport(startDate: LocalDate): List<ArtificialReportVo>

    fun cReport(startDate: LocalDate): List<ArtificialCReportVo>

}