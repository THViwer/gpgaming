package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.WalletNote
import com.onepiece.gpgaming.beans.value.database.WalletNoteQuery

interface WalletNoteService {

//    fun my(clientId: Int, memberId: Int): List<WalletNote>

    fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote>
}