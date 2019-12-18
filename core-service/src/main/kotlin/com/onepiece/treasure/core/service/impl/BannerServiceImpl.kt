package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.BannerType
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.BannerDao
import com.onepiece.treasure.core.service.BannerService
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class BannerServiceImpl(
        private val bannerDao: BannerDao,
        private val redisService: RedisService
) : BannerService {

    override fun all(clientId: Int): List<Banner> {
        val redisKey = OnePieceRedisKeyConstant.banners(clientId)
        return redisService.getList(redisKey, Banner::class.java) {
            bannerDao.all(clientId)
        }
    }

    override fun findByType(clientId: Int, type: BannerType): List<Banner> {
        return this.all(clientId).filter { it.type == type }
    }

    override fun create(bannerCo: BannerCo): Int {
        val id = bannerDao.create(bannerCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.banners(bannerCo.clientId))
        return id
    }

    override fun update(bannerUo: BannerUo) {

        val advert = bannerDao.get(bannerUo.id)

        val state = bannerDao.update(bannerUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }


        redisService.delete(OnePieceRedisKeyConstant.banners(advert.clientId))
    }
}