package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.WalletNote
import com.onepiece.gpgaming.beans.value.database.WalletNoteCo
import com.onepiece.gpgaming.beans.value.database.WalletNoteQuery
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface WalletNoteDao: BasicDao<WalletNote> {

    fun total(walletNoteQuery: WalletNoteQuery): Int

    fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote>

    fun create(walletNoteCo: WalletNoteCo): Boolean

}