package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.WebSite
import com.onepiece.treasure.beans.value.database.WebSiteCo
import com.onepiece.treasure.beans.value.database.WebSiteUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.WebSiteDao
import com.onepiece.treasure.core.service.WebSiteService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class WebSiteServiceImpl(
        private val webSiteDao: WebSiteDao,
        private val redisService: RedisService
): WebSiteService {

    override fun all(clientId: Int): List<WebSite> {
        val redisKey = OnePieceRedisKeyConstant.webSite(clientId)
        return redisService.getList(redisKey, WebSite::class.java) {
            webSiteDao.all(clientId)
        }
    }

    override fun create(webSiteCo: WebSiteCo) {

        val state = webSiteDao.create(webSiteCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.webSite(webSiteCo.clientId))
    }

    override fun update(webSiteUo: WebSiteUo) {

        val hasWebSite = webSiteDao.get(webSiteUo.id)

        val state = webSiteDao.update(webSiteUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.webSite(hasWebSite.clientId))
    }
}