package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.WalletCo
import com.onepiece.treasure.core.dao.value.WalletUo
import com.onepiece.treasure.core.model.Wallet

interface WalletDao: BasicDao<Wallet> {

    fun create(walletCo: WalletCo): Boolean

    fun update(walletUo: WalletUo): Boolean


}