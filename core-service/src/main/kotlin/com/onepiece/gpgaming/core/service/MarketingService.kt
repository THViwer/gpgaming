package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Marketing
import com.onepiece.gpgaming.beans.value.database.MarketingValue


interface MarketingService {

    fun find(clientId: Int): List<Marketing>

    fun create(co: MarketingValue.MarketingCo)

    fun update(uo:  MarketingValue.MarketingUo)

}