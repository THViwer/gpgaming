package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.WalletEvent
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.dao.WalletDao
import com.onepiece.treasure.core.dao.WalletNoteDao
import com.onepiece.treasure.core.service.WalletService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WalletServiceImpl(
        private val walletDao: WalletDao,
        private val walletNoteDao: WalletNoteDao
) : WalletService {

    override fun getMemberWallet(memberId: Int): Wallet {
        return walletDao.getMemberWallet(memberId)
    }

    override fun create(walletCo: WalletCo) {
        val state = walletDao.create(walletCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(walletUo: WalletUo): BigDecimal {

        val wallet = this.getMemberWallet(walletUo.memberId)

        val state = when (walletUo.event) {

            WalletEvent.DEPOSIT -> {
                val walletDepositUo = WalletDepositUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.deposit(walletDepositUo)
            }
            WalletEvent.FREEZE -> {
                val walletFreezeUo = WalletFreezeUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.freeze(walletFreezeUo)
            }
            WalletEvent.WITHDRAW -> {
                val walletWithdrawUo = WalletWithdrawUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.withdraw(walletWithdrawUo)
            }
            WalletEvent.WITHDRAW_FAIL -> {
                val walletWithdrawUo = WalletWithdrawUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.withdrawFail(walletWithdrawUo)
            }
            WalletEvent.TRANSFER_IN, WalletEvent.Artificial -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
            WalletEvent.TRANSFER_IN_ROLLBACK -> {
                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = BigDecimal.ZERO)
                walletDao.transferOut(transferOutUo)
            }
            WalletEvent.TRANSFER_OUT -> {
                check(wallet.balance >= walletUo.money) { OnePieceExceptionCode.BALANCE_NOT_WORTH}

                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = walletUo.giftBalance?: BigDecimal.ZERO)
                walletDao.transferOut(transferOutUo)
            }
            WalletEvent.TRANSFER_OUT_ROLLBACK -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
        }

        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // TODO async insert wallet note
        val walletNoteCo = WalletNoteCo(clientId = walletUo.clientId, memberId = wallet.memberId, event = walletUo.event, remarks = walletUo.remarks,
                waiterId = walletUo.waiterId, eventId = walletUo.eventId, money = walletUo.money, promotionMoney = walletUo.giftBalance)
        val wnState = walletNoteDao.create(walletNoteCo)
        check(wnState) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        return wallet.balance.plus(walletUo.money)
    }

    override fun query(walletQuery: WalletQuery): List<Wallet> {
        return walletDao.query(walletQuery)
    }
}