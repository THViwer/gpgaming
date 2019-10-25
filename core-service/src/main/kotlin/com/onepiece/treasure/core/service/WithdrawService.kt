package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo

interface WithdrawService {

    fun query(withdrawQuery: WithdrawQuery): List<Withdraw>

    fun create(withdrawCo: WithdrawCo)

    fun update(withdrawUo: WithdrawUo)

}