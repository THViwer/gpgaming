package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.WalletNote
import com.onepiece.treasure.beans.value.database.WalletNoteQuery

interface WalletNoteService {

//    fun my(clientId: Int, memberId: Int): List<WalletNote>

    fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote>
}