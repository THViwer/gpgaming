package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo

interface TransferOrderService {

    fun create(transferOrderCo: TransferOrderCo)

    fun update(transferOrderUo: TransferOrderUo)

//    fun report(member: Int?, startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>

//    fun reportByClient(clientId: Int, startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>


}