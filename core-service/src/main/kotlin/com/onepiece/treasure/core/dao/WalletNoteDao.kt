package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.WalletNoteCo
import com.onepiece.treasure.beans.value.database.WalletNoteQuery
import com.onepiece.treasure.beans.model.WalletNote

interface WalletNoteDao: BasicDao<WalletNote> {

    fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote>

    fun create(walletNoteCo: WalletNoteCo): Boolean

}