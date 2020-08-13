package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Market
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.dao.MarketDao
import com.onepiece.gpgaming.core.service.MarketService
import org.springframework.stereotype.Service

@Service
class MarketServiceImpl(
        private val marketingDao: MarketDao
) : MarketService {

    override fun get(id: Int): Market {
        return  marketingDao.get(id  = id)
    }

    override fun find(clientId: Int): List<Market> {
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