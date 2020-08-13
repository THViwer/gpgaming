package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Marketing
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao


interface MarketingDao : BasicDao<Marketing> {

    fun create(co: MarketingValue.MarketingCo): Boolean

    fun update(uo:  MarketingValue.MarketingUo): Boolean

}