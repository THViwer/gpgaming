package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.WalletNote

interface WalletNoteService {

    fun my(clientId: Int, memberId: Int): List<WalletNote>

}