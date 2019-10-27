package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.dao.basic.BasicDao

interface WalletDao: BasicDao<Wallet> {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo): Boolean

//    fun update(walletUo: WalletUo): Boolean
//
//    fun transfer(walletUo: WalletUo): Boolean

    fun freeze(walletFreezeUo: WalletFreezeUo): Boolean

    fun deposit(walletDepositUo: WalletDepositUo): Boolean

    fun withdraw(walletWithdrawUo: WalletWithdrawUo): Boolean

    fun transferIn(walletTransferInUo: WalletTransferInUo): Boolean

    fun transferOut(walletTransferOutUo: WalletTransferOutUo): Boolean


//    fun bet(walletUo: WalletUo): Boolean


}