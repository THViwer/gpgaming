package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.beans.value.database.ClientPlatformTransferReportVo
import com.onepiece.treasure.beans.value.database.MemberTransferReportVo
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface TransferOrderDao: BasicDao<TransferOrder> {

    fun create(transferOrderCo: TransferOrderCo): Boolean

    fun update(transferOrderUo: TransferOrderUo): Boolean

    fun report(startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo>

    fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo>

}