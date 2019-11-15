package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.model.WalletNote
import com.onepiece.treasure.beans.value.database.WalletNoteQuery
import com.onepiece.treasure.core.dao.WalletNoteDao
import com.onepiece.treasure.core.service.WalletNoteService
import org.springframework.stereotype.Service

@Service
class WalletNoteServiceImpl(
        private val walletNoteDao: WalletNoteDao
) : WalletNoteService {

    override fun my(clientId: Int, memberId: Int): List<WalletNote> {
        val walletNoteQuery = WalletNoteQuery(clientId = clientId, memberId = memberId, event = null)
        return walletNoteDao.query(walletNoteQuery)
    }
}