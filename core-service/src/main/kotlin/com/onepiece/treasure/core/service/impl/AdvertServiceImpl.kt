package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.BannerDao
import com.onepiece.treasure.core.service.BannerService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class AdvertServiceImpl(
        private val advertDao: BannerDao,
        private val redisService: RedisService
) : BannerService {

    override fun all(clientId: Int): List<Banner> {
        val redisKey = OnePieceRedisKeyConstant.adverts(clientId)
        return redisService.getList(redisKey, Banner::class.java) {
            advertDao.all(clientId)
        }
    }

    override fun create(bannerCo: BannerCo) {
        val state = advertDao.create(bannerCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.adverts(bannerCo.clientId))
    }

    override fun update(bannerUo: BannerUo) {

        val advert = advertDao.get(bannerUo.id)

        val state = advertDao.update(bannerUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }


        redisService.delete(OnePieceRedisKeyConstant.adverts(advert.clientId))
    }
}