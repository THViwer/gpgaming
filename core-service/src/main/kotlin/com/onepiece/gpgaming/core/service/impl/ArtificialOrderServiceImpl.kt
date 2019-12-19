package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.core.dao.ArtificialOrderDao
import com.onepiece.gpgaming.core.service.ArtificialOrderService
import com.onepiece.gpgaming.core.service.WalletService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArtificialOrderServiceImpl(
        private val artificialOrderDao: ArtificialOrderDao,
        private val walletService: WalletService
) : ArtificialOrderService {

    override fun query(query: ArtificialOrderQuery): Page<ArtificialOrder> {

        val total = artificialOrderDao.total(query)
        if (total == 0) return Page.empty()

        val data = artificialOrderDao.query(query)
        return Page.of(total, data)

    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(artificialOrderCo: ArtificialOrderCo) {

        val walletUo = WalletUo(clientId = artificialOrderCo.clientId, memberId = artificialOrderCo.memberId, money = artificialOrderCo.money,
                event = WalletEvent.Artificial, eventId = artificialOrderCo.orderId, remarks = artificialOrderCo.remarks, waiterId = artificialOrderCo.operatorId)
        walletService.update(walletUo)

        val state = artificialOrderDao.create(artificialOrderCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}