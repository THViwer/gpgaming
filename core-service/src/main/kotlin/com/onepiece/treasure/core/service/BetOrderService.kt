package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.BetOrder
import com.onepiece.treasure.beans.value.database.BetOrderValue

interface BetOrderService {

    fun batch(orders: List<BetOrderValue.BetOrderCo>)

    fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder>

    fun getNotMarkBets(tableSequence: Int): List<BetOrderValue.BetMarkVo>

}