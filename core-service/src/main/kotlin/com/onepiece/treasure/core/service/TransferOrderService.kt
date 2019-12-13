package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderReportVo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.beans.value.internet.web.TransferOrderValue
import java.time.LocalDate

interface TransferOrderService {

    fun create(transferOrderCo: TransferOrderCo)

    fun update(transferOrderUo: TransferOrderUo)

    fun query(query: TransferOrderValue.Query): List<TransferOrder>

    fun report(startDate: LocalDate): List<TransferOrderReportVo>

//    fun report(member: Int?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>

//    fun reportByClient(clientId: Int, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>


}