package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.beans.value.internet.web.ClientPlatformTransferReportVo
import com.onepiece.treasure.beans.value.internet.web.MemberTransferReportVo
import java.time.LocalDate

interface TransferOrderService {

    fun create(transferOrderCo: TransferOrderCo)

    fun update(transferOrderUo: TransferOrderUo)

    fun report(startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>


}