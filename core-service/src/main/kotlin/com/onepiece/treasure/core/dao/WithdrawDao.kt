package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface WithdrawDao: BasicDao<Withdraw> {

    fun findWithdraw(clientId: Int, orderId: String): Withdraw

    fun query(query: WithdrawQuery, current: Int, size: Int): List<Withdraw>

    fun total(query: WithdrawQuery): Int

    fun create(orderCo: WithdrawCo): Boolean

    fun lock(withdrawLockUo: DepositLockUo): Boolean

    fun update(orderUo: WithdrawUo): Boolean

}