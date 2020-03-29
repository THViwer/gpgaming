package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.TransferOrder
import com.onepiece.gpgaming.beans.value.database.TransferOrderCo
import com.onepiece.gpgaming.beans.value.database.TransferOrderReportVo
import com.onepiece.gpgaming.beans.value.database.TransferOrderUo
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.service.TransferOrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class TransferOrderServiceImpl(
        private val transferOrderDao: TransferOrderDao
) : TransferOrderService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun create(transferOrderCo: TransferOrderCo) {
        val state = transferOrderDao.create(transferOrderCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(transferOrderUo: TransferOrderUo) {
        val state = transferOrderDao.update(transferOrderUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun logPromotionEnd(clientId: Int, memberId: Int, promotionId: Int, transferOutAmount: BigDecimal) {

        val query = TransferOrderValue.Query(clientId = clientId, memberId = memberId, promotionId = promotionId, current = 0, size = 1, from = null, username = null,
                startDate = null, endDate = null)
        val order = this.query(query).firstOrNull()

        if (order !=  null) {
            val transferOrderUo = TransferOrderUo(orderId = order.orderId, state = null, transferOutAmount = transferOutAmount)
            transferOrderDao.update(transferOrderUo)
        }

    }

    override fun query(query: TransferOrderValue.Query): List<TransferOrder> {
        return transferOrderDao.query(query)
    }

    override fun report(startDate: LocalDate): List<TransferOrderReportVo> {
        return transferOrderDao.report(startDate)
    }

    override fun queryLastPromotion(clientId: Int, memberId: Int, startTime: LocalDateTime): List<TransferOrder> {
        return transferOrderDao.queryLastPromotion(clientId, memberId, startTime)
    }

    //    override fun report(startDate: LocalDate, endDate: LocalDate): List<MemberTransferReportVo> {
//        return transferOrderDao.report(startDate, endDate)
//    }
//
//    override fun reportByClient(startDate: LocalDate, endDate: LocalDate): List<ClientPlatformTransferReportVo> {
//        return transferOrderDao.reportByClient(startDate, endDate)
//    }
}