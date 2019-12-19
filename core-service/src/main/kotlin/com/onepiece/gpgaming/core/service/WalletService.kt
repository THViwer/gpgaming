package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.database.WalletUo
import java.math.BigDecimal

interface WalletService {

    fun getMemberWallet(memberId: Int): Wallet

    fun create(walletCo: WalletCo)

    fun update(walletUo: WalletUo): BigDecimal

    fun query(walletQuery: WalletQuery): List<Wallet>

}