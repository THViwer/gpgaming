package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Advert
import com.onepiece.treasure.beans.value.database.AdvertCo
import com.onepiece.treasure.beans.value.database.AdvertUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.AdvertDao
import com.onepiece.treasure.core.service.AdvertService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class AdvertServiceImpl(
        private val advertDao: AdvertDao,
        private val redisService: RedisService
) : AdvertService {

    override fun all(clientId: Int): List<Advert> {
        val redisKey = OnePieceRedisKeyConstant.adverts(clientId)
        return redisService.getList(redisKey, Advert::class.java) {
            advertDao.all(clientId)
        }
    }

    override fun create(advertCo: AdvertCo) {
        val state = advertDao.create(advertCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.adverts(advertCo.clientId))
    }

    override fun update(advertUo: AdvertUo) {

        val advert = advertDao.get(advertUo.id)

        val state = advertDao.update(advertUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }


        redisService.delete(OnePieceRedisKeyConstant.adverts(advert.clientId))
    }
}