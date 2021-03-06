package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.core.dao.PayOrderDao
import com.onepiece.gpgaming.core.service.PayOrderService
import com.onepiece.gpgaming.core.service.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PayOrderServiceImpl(
        private val payOrderDao: PayOrderDao
) : PayOrderService {

    @Autowired
    lateinit var walletService: WalletService

    override fun page(query: PayOrderValue.PayOrderQuery): Page<PayOrder> {
        val total = payOrderDao.total(query)
        if (total == 0) return Page.empty()

        val data = payOrderDao.query(query)
        return Page.of(total = total, data = data)
    }

    override fun query(query: PayOrderValue.PayOrderQuery): List<PayOrder> {
        return payOrderDao.query(query)
    }

    override fun summary(query: PayOrderValue.PayOrderQuery): List<PayOrderValue.ThirdPaySummary> {
        return payOrderDao.summary(query = query)
    }

    override fun create(co: PayOrderValue.PayOrderCo) {
        val flag = payOrderDao.create(co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun check(uo: PayOrderValue.ConstraintUo) {

        val order = payOrderDao.find(orderId = uo.orderId)
        if (order.state == PayState.Successful) error(OnePieceExceptionCode.DATA_FAIL)

        val flag = payOrderDao.check(uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 更新饭钱
        val walletUo = WalletUo(clientId = order.clientId, waiterId = null, memberId = order.memberId, money = order.amount, giftBalance = null,
                event = WalletEvent.ThirdPay, eventId = order.orderId, remarks = uo.remark)
        walletService.update(walletUo = walletUo)
    }

    override fun successful(orderId: String, thirdOrderId: String) {

        // 查询订单
        val order = payOrderDao.find(orderId = orderId)
        if (order.state == PayState.Successful) return

        // 更新支付订单
        val flag = payOrderDao.successful(orderId, thirdOrderId)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 更新钱包
        val walletUo = WalletUo(clientId = order.clientId, waiterId = null, memberId = order.memberId, money = order.amount, giftBalance = null,
                event = WalletEvent.ThirdPay, eventId = order.orderId, remarks = "system auto pay")
        walletService.update(walletUo = walletUo)

    }

    override fun failed(orderId: String) {
        payOrderDao.failed(orderId)
    }

    override fun close(closeTime: LocalDateTime) {
        payOrderDao.close(closeTime)
    }

    override fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal {
        return payOrderDao.sumSuccessful(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
    }
}