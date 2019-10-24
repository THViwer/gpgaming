package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.WalletNoteCo
import com.onepiece.treasure.core.model.WalletNote
import com.onepiece.treasure.core.model.WalletQuery

interface WalletNoteDao: BasicDao<WalletNote> {

    fun query(walletQuery: WalletQuery): List<WalletNote>

    fun create(walletNoteCo: WalletNoteCo): Boolean

}