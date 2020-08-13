package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Market
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao


interface MarketDao : BasicDao<Market> {

    fun create(co: MarketingValue.MarketingCo): Boolean

    fun update(uo:  MarketingValue.MarketingUo): Boolean

}