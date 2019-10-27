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

    override fun update(walletUo: WalletUo) {

        val wallet = this.getMemberWallet(walletUo.memberId)

         val state = when (walletUo.event) {

            WalletEvent.DEPOSIT -> {
                val walletDepositUo = WalletDepositUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.deposit(walletDepositUo)
            }
            WalletEvent.WITHDRAW -> {
                val walletWithdrawUo = WalletWithdrawUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.withdraw(walletWithdrawUo)

            }
            WalletEvent.TRANSFER_IN -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
            WalletEvent.TRANSFER_OUT -> {
                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = walletUo.giftBalance)
                walletDao.transferOut(transferOutUo)
            }
        }

        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // TODO async insert wallet note
        val walletNoteCo = WalletNoteCo(clientId = walletUo.clientId, memberId = wallet.memberId, event = walletUo.event, remarks = walletUo.remarks)
        val wnState = walletNoteDao.create(walletNoteCo)
        check(wnState) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}