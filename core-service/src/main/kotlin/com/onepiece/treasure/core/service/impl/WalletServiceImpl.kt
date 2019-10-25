package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletNoteCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.core.dao.WalletDao
import com.onepiece.treasure.core.dao.WalletNoteDao
import com.onepiece.treasure.core.service.WalletService
import org.springframework.stereotype.Service

@Service
class WalletServiceImpl(
        private val walletDao: WalletDao,
        private val walletNoteDao: WalletNoteDao
) : WalletService {

    override fun getMemberWallet(memberId: Int): Wallet {
        return walletDao.get(memberId)
    }

    override fun create(walletCo: WalletCo) {
        val state = walletDao.create(walletCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(walletUo: WalletUo) {

        val wallet = this.getMemberWallet(walletUo.memberId)

        val state = walletDao.update(walletUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // TODO async insert wallet note
        val walletNoteCo = WalletNoteCo(clientId = walletUo.clientId, memberId = wallet.memberId, event = walletUo.event, remarks = walletUo.remarks)
        val wnState = walletNoteDao.create(walletNoteCo)
        check(wnState) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}