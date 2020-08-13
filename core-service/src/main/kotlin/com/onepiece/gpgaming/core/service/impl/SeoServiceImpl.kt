package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.ClientConfigDao
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class SeoServiceImpl(
        private val seoDao: ClientConfigDao,
        private val redisService: RedisService
) : ClientConfigService {

    override fun get(clientId: Int): ClientConfig {

        val redisKey = OnePieceRedisKeyConstant.getSeo(clientId)

        return redisService.get(redisKey, ClientConfig::class.java) {
            val list = seoDao.all(clientId)

            if (list.isEmpty()) {
                val seoUo = ClientConfigValue.ClientConfigUo(clientId = clientId, title = "", keywords = "", description = "",
                        googleStatisticsId = "", liveChatId = "", facebookTr = "", liveChatTab = true, asgContent = "",
                        facebookShowPosition = ShowPosition.Index)
                seoDao.create(seoUo)
                seoDao.all(clientId).first()
            } else {
                list.first()
            }
        }!!

    }

    override fun update(configUo: ClientConfigValue.ClientConfigUo) {
        seoDao.update(configUo)

        val redisKey = OnePieceRedisKeyConstant.getSeo(configUo.clientId)
        redisService.delete(redisKey)
    }
}