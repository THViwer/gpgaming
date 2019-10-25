package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Balance
import com.onepiece.treasure.beans.value.database.BalanceCo
import com.onepiece.treasure.beans.value.database.BalanceUo

interface BalanceService {

    fun getClientBalance(clientId: Int): Balance

    fun create(balanceCo: BalanceCo)

    fun update(balanceUo: BalanceUo)

}