package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Marketing
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.dao.MarketingDao
import com.onepiece.gpgaming.core.service.MarketingService
import org.springframework.stereotype.Service

@Service
class MarketingServiceImpl(
        private val marketingDao: MarketingDao
) : MarketingService {

    override fun find(clientId: Int): List<Marketing> {
        return marketingDao.all(clientId)
    }

    override fun create(co: MarketingValue.MarketingCo) {
        val flag = marketingDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(uo: MarketingValue.MarketingUo) {
        val flag = marketingDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}