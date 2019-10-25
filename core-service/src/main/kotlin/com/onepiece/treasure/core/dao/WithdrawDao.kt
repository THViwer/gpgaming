package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Withdraw
import com.onepiece.treasure.beans.value.database.WithdrawCo
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.database.WithdrawUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface WithdrawDao: BasicDao<Withdraw> {

    fun query(query: WithdrawQuery): List<Withdraw>

    fun create(orderCo: WithdrawCo): Boolean

    fun update(orderUo: WithdrawUo): Boolean

}