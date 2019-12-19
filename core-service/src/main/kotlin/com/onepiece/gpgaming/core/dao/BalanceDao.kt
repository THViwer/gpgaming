package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.value.database.BalanceCo
import com.onepiece.gpgaming.beans.value.database.BalanceUo
import com.onepiece.gpgaming.beans.model.Balance
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface BalanceDao: BasicDao<Balance> {

    fun getClientBalance(clientId: Int): Balance

    fun create(balanceCo: BalanceCo): Boolean

    fun update(balanceUo: BalanceUo): Boolean

}