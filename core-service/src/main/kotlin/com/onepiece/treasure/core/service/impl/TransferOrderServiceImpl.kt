package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.TransferOrder
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderReportVo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.beans.value.internet.web.TransferOrderValue
import com.onepiece.treasure.core.dao.TransferOrderDao
import com.onepiece.treasure.core.service.TransferOrderService
import org.springframework.stereotype.Service
import java.time.LocalDate


@Service
class TransferOrderServiceImpl(
        private val transferOrderDao: TransferOrderDao
) : TransferOrderService {

    override fun create(transferOrderCo: TransferOrderCo) {
        val state = transferOrderDao.create(transferOrderCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(transferOrderUo: TransferOrderUo) {
        val state = transferOrderDao.update(transferOrderUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun query(query: TransferOrderValue.Query): List<TransferOrder> {
        return transferOrderDao.query(query)
    }

    override fun report(startDate: LocalDate): List<TransferOrderReportVo> {
        return transferOrderDao.report(startDate)
    }

    //    override fun report(startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo> {
//        return transferOrderDao.report(startDate, endDate)
//    }
//
//    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo> {
//        return transferOrderDao.reportByClient(startDate, endDate)
//    }
}