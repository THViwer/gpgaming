package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface WalletDao: BasicDao<Wallet> {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo): Boolean

    fun update(walletUo: WalletUo): Boolean

    fun transfer(walletUo: WalletUo): Boolean

//    fun bet(walletUo: WalletUo): Boolean


}