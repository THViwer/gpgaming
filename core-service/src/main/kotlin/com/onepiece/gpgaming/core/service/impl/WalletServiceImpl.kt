package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.beans.value.database.WalletDepositUo
import com.onepiece.gpgaming.beans.value.database.WalletFreezeUo
import com.onepiece.gpgaming.beans.value.database.WalletNoteCo
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.database.WalletTransferInUo
import com.onepiece.gpgaming.beans.value.database.WalletTransferOutUo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.beans.value.database.WalletWithdrawUo
import com.onepiece.gpgaming.core.dao.WalletDao
import com.onepiece.gpgaming.core.dao.WalletNoteDao
import com.onepiece.gpgaming.core.service.WalletService
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

            WalletEvent.Rebate,
            WalletEvent.Commission,
            WalletEvent.ThirdPay,
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
                walletDao.transferOut(transferOutUo, -1)
            }
            WalletEvent.TRANSFER_OUT -> {
                check(wallet.balance >= walletUo.money) { OnePieceExceptionCode.BALANCE_NOT_WORTH}

                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = walletUo.giftBalance ?: BigDecimal.ZERO)
                walletDao.transferOut(transferOutUo, 1)
            }
            WalletEvent.TRANSFER_OUT_ROLLBACK -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
        }

        val afterMoney: BigDecimal
        val money: BigDecimal
        when (walletUo.event) {
            WalletEvent.FREEZE -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance.minus(walletUo.money)
            }
            WalletEvent.Rebate,
            WalletEvent.Commission,
            WalletEvent.ThirdPay,
            WalletEvent.DEPOSIT,
            WalletEvent.WITHDRAW_FAIL,
            WalletEvent.TRANSFER_IN,
            WalletEvent.Artificial,
            WalletEvent.TRANSFER_OUT_ROLLBACK -> {
                money = walletUo.money
                afterMoney = wallet.balance.plus(walletUo.money)
            }
            WalletEvent.TRANSFER_IN_ROLLBACK,
            WalletEvent.TRANSFER_OUT -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance.minus(walletUo.money)
            }
            WalletEvent.WITHDRAW -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance
            }

        }

        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // TODO async insert wallet note
        val walletNoteCo = WalletNoteCo(clientId = walletUo.clientId, memberId = wallet.memberId, event = walletUo.event, remarks = walletUo.remarks,
                waiterId = walletUo.waiterId, eventId = walletUo.eventId, money = money, promotionMoney = walletUo.giftBalance, originMoney = wallet.balance,
                afterMoney = afterMoney)
        val wnState = walletNoteDao.create(walletNoteCo)
        check(wnState) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        return wallet.balance.plus(walletUo.money)
    }

    override fun query(walletQuery: WalletQuery): List<Wallet> {
        return walletDao.query(walletQuery)
    }
}