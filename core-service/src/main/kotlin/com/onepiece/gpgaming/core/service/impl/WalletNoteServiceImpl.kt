package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.WalletNote
import com.onepiece.gpgaming.beans.value.database.WalletNoteQuery
import com.onepiece.gpgaming.core.dao.WalletNoteDao
import com.onepiece.gpgaming.core.service.WalletNoteService
import org.springframework.stereotype.Service

@Service
class WalletNoteServiceImpl(
        private val walletNoteDao: WalletNoteDao
) : WalletNoteService {

//    override fun my(clie/**/ntId: Int, memberId: Int): List<WalletNote> {
//        val walletNoteQuery = WalletNoteQuery(clientId = clientId, memberId = memberId, event = null)
//        return walletNoteDao.query(walletNoteQuery)
//    }

    override fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote> {
        return walletNoteDao.query(walletNoteQuery)
    }
}