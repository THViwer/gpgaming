package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.ClientConfigDao
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class ClientConfigServiceImpl(
        private val clientConfigDao: ClientConfigDao,
        private val redisService: RedisService
) : ClientConfigService {

    override fun get(clientId: Int): ClientConfig {

        val redisKey = OnePieceRedisKeyConstant.getSeo(clientId)

        return redisService.get(redisKey, ClientConfig::class.java) {
            val list = clientConfigDao.all(clientId)

            if (list.isEmpty()) {
                val seoUo = ClientConfigValue.ClientConfigUo(clientId = clientId, title = "", keywords = "", description = "",
                        googleStatisticsId = "", liveChatId = "", facebookTr = "", liveChatTab = true, asgContent = "",
                        facebookShowPosition = ShowPosition.Index)
                clientConfigDao.create(seoUo)
                clientConfigDao.all(clientId).first()
            } else {
                list.first()
            }
        }!!

    }

    override fun update(configUo: ClientConfigValue.ClientConfigUo) {
        clientConfigDao.update(configUo)

        val redisKey = OnePieceRedisKeyConstant.getSeo(configUo.clientId)
        redisService.delete(redisKey)
    }

    override fun update(id: Int, enableRegisterMessage: Boolean, registerMessageTemplate: String) {
        clientConfigDao.update(id = id, enableRegisterMessage = enableRegisterMessage, registerMessageTemplate = registerMessageTemplate)

        val config = clientConfigDao.get(id = id)
        val redisKey = OnePieceRedisKeyConstant.getSeo(config.clientId)
        redisService.delete(redisKey)
    }

    override fun update(uo: ClientConfigValue.IntroduceUo) {
        val flag = clientConfigDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        val redisKey = OnePieceRedisKeyConstant.getSeo(uo.clientId)
        redisService.delete(redisKey)
    }
}