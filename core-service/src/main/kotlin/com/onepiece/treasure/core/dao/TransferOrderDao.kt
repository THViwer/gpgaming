package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.TransferOrderValue
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface TransferOrderDao: BasicDao<TransferOrder> {

    fun create(transferOrderCo: TransferOrderCo): Boolean

    fun update(transferOrderUo: TransferOrderUo): Boolean

    fun memberPlatformReport(query: TransferReportQuery): List<MemberTransferPlatformReportVo>

    fun memberReport(query: TransferReportQuery): List<MemberTransferReportVo>

    fun clientPlatformReport(query: TransferReportQuery): List<ClientTransferPlatformReportVo>

    fun clientReport(query: TransferReportQuery): List<ClientTransferReportVo>

    fun query(query: TransferOrderValue.Query): List<TransferOrder>

    fun report(startDate: LocalDate): List<TransferOrderReportVo>

//    fun reportByPlatform(memberId: Int?, from: Platform?, to: Platform?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferPlatformReportVo>
//
//    fun report(memberId: Int?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>
//
//    fun clientReport(clientId: Int?, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>
//
//    fun clientReportByPlatform(clientId: Int, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>

}

data class TransferReportQuery(

        val clientId: Int?,

        val memberId: Int?,

        val from: Platform?,

        val to: Platform?,

        val startDate: LocalDate,

        val endDate: LocalDate
)