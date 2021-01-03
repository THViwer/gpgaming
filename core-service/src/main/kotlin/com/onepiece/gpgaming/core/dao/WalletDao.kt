package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.beans.value.database.WalletDepositUo
import com.onepiece.gpgaming.beans.value.database.WalletFreezeUo
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.database.WalletTransferInUo
import com.onepiece.gpgaming.beans.value.database.WalletTransferOutUo
import com.onepiece.gpgaming.beans.value.database.WalletWithdrawUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface WalletDao: BasicDao<Wallet> {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo): Boolean

//    fun update(walletUo: WalletUo): Boolean
//
//    fun transfer(walletUo: WalletUo): Boolean

    fun freeze(walletFreezeUo: WalletFreezeUo): Boolean

    fun otherAddAmount(walletDepositUo: WalletDepositUo): Boolean

    fun deposit(walletDepositUo: WalletDepositUo): Boolean

    fun withdraw(walletWithdrawUo: WalletWithdrawUo): Boolean

    fun withdrawFail(walletWithdrawUo: WalletWithdrawUo): Boolean

    fun transferIn(walletTransferInUo: WalletTransferInUo): Boolean

    fun transferOut(walletTransferOutUo: WalletTransferOutUo, frequency: Int): Boolean

    fun rebate(walletTransferInUo: WalletTransferInUo): Boolean

    fun query(walletQuery: WalletQuery): List<Wallet>


//    fun bet(walletUo: WalletUo): Boolean


}