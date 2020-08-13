package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Market
import com.onepiece.gpgaming.beans.value.database.MarketingValue


interface MarketService {

    fun get(id: Int): Market

    fun find(clientId: Int): List<Market>

    fun create(co: MarketingValue.MarketingCo)

    fun update(uo:  MarketingValue.MarketingUo)

}