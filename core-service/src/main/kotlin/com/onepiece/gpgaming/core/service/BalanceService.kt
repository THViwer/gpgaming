package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Balance
import com.onepiece.gpgaming.beans.value.database.BalanceCo
import com.onepiece.gpgaming.beans.value.database.BalanceUo

interface BalanceService {

    fun getClientBalance(clientId: Int): Balance

    fun create(balanceCo: BalanceCo)

    fun update(balanceUo: BalanceUo)

}