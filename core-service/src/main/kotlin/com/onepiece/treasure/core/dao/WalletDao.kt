package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.beans.model.Wallet

interface WalletDao: BasicDao<Wallet> {

    fun create(walletCo: WalletCo): Boolean

    fun update(walletUo: WalletUo): Boolean


}