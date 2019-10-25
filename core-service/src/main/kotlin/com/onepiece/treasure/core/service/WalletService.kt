package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.WalletCo
import com.onepiece.treasure.beans.value.database.WalletUo

interface WalletService {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo)

    fun update(walletUo: WalletUo)

}