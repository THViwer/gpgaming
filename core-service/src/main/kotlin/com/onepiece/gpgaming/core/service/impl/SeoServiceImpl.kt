package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.SeoDao
import com.onepiece.gpgaming.core.service.SeoService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class SeoServiceImpl(
        private val seoDao: SeoDao,
        private val redisService: RedisService
) : SeoService {

    override fun get(clientId: Int): Seo {

        val redisKey = OnePieceRedisKeyConstant.getSeo(clientId)

        return redisService.get(redisKey, Seo::class.java) {
            val list = seoDao.all(clientId)

            if (list.isEmpty()) {
                val seoUo = SeoValue.SeoUo(clientId = clientId, title = "", keywords = "", description = "", googleStatisticsId = "", liveChatId = "", facebookTr = "",
                        liveChatTab = true)
                seoDao.create(seoUo)
                seoDao.all(clientId).first()
            } else {
                list.first()
            }
        }!!

    }

    override fun update(seoUo: SeoValue.SeoUo) {
        seoDao.update(seoUo)

        val redisKey = OnePieceRedisKeyConstant.getSeo(seoUo.clientId)
        redisService.delete(redisKey)
    }
}