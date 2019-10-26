package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo

interface WithdrawService {

    fun findWithdraw(clientId: Int, orderId: String): Withdraw

    fun query(withdrawQuery: WithdrawQuery): List<Withdraw>

    fun query(withdrawQuery: WithdrawQuery, current: Int, size: Int): Page<Withdraw>

    fun create(withdrawCo: WithdrawCo)

    fun lock(withdrawLockUo: DepositLockUo)

    fun update(withdrawUo: WithdrawUo)

}