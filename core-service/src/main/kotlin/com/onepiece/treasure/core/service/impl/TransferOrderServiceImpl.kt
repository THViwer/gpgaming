package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.TransferOrderCo
import com.onepiece.treasure.beans.value.database.TransferOrderUo
import com.onepiece.treasure.core.dao.TransferOrderDao
import com.onepiece.treasure.core.service.TransferOrderService
import org.springframework.stereotype.Service


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
}