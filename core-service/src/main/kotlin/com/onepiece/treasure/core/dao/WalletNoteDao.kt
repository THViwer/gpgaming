package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.WalletNoteCo
import com.onepiece.treasure.core.dao.value.WalletNoteQuery
import com.onepiece.treasure.core.model.WalletNote

interface WalletNoteDao: BasicDao<WalletNote> {

    fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote>

    fun create(walletNoteCo: WalletNoteCo): Boolean

}