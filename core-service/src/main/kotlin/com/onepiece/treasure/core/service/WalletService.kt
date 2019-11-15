package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Wallet
import com.onepiece.treasure.beans.value.database.*
import java.math.BigDecimal

interface WalletService {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo)

    fun update(walletUo: WalletUo): BigDecimal

    fun query(walletQuery: WalletQuery): List<Wallet>

}