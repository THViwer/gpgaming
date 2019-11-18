package com.onepiece.treasure.games.bet

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.value.database.BetOrderValue

interface CenterBetOrder {

    fun getBetOrders(objectMapper: ObjectMapper): List<BetOrderValue.BetOrderCo>

//    fun getBetOrder(objectMapper: ObjectMapper): BetOrderValue.BetOrderCo

}

