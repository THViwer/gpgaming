package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.database.ClientTransferPlatformReportVo
import com.onepiece.gpgaming.beans.value.database.ClientTransferReportVo
import com.onepiece.gpgaming.beans.value.database.MemberTransferPlatformReportVo
import com.onepiece.gpgaming.beans.value.database.MemberTransferReportVo
import com.onepiece.gpgaming.beans.value.database.TransferOrderCo
import com.onepiece.gpgaming.beans.value.database.TransferOrderReportVo
import com.onepiece.gpgaming.beans.value.database.TransferOrderUo
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate
import java.time.LocalDateTime

interface TransferOrderDao: BasicDao<TransferOrder> {

    fun create(transferOrderCo: TransferOrderCo): Boolean

    fun update(transferOrderUo: TransferOrderUo): Boolean

    fun memberPlatformReport(query: TransferReportQuery): List<MemberTransferPlatformReportVo>

    fun memberReport(query: TransferReportQuery): List<MemberTransferReportVo>

    fun clientPlatformReport(query: TransferReportQuery): List<ClientTransferPlatformReportVo>

    fun clientReport(query: TransferReportQuery): List<ClientTransferReportVo>

    fun query(query: TransferOrderValue.Query): List<TransferOrder>

    fun queryLastPromotion(clientId: Int, memberId: Int, startTime: LocalDateTime): List<TransferOrder>

    fun report(startDate: LocalDate): List<TransferOrderReportVo>

    fun queryActiveCount(startDate: LocalDate, endDate: LocalDate): List<TransferActiveCount>

    fun queryProcessOrder(startDate: LocalDate, endDate: LocalDate): List<TransferOrder>

//    fun reportByPlatform(memberId: Int?, from: Platform?, to: Platform?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferPlatformReportVo>
//
//    fun report(memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>
//
//    fun clientReport(clientId: Int?, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>
//
//    fun clientReportByPlatform(clientId: Int, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>

}

data class TransferActiveCount(

        val clientId: Int,

        val platform:  Platform,

        val count: Int
)

data class TransferReportQuery(

        val clientId: Int?,

        val memberId: Int?,

        val from: Platform?,

        val to: Platform?,

        val startDate: LocalDate,

        val endDate: LocalDate
)