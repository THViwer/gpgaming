package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.database.WebSiteCo
import com.onepiece.gpgaming.beans.value.database.WebSiteUo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.WebSiteDao
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WebSiteServiceImpl(
        private val webSiteDao: WebSiteDao,
        private val redisService: RedisService
): WebSiteService {
    private val log = LoggerFactory.getLogger(WebSiteServiceImpl::class.java)

    override fun all(): List<WebSite> {
        val redisKey = OnePieceRedisKeyConstant.getAllWebSite()
        return redisService.getList(redisKey, WebSite::class.java) {
            webSiteDao.all()
        }
    }

    override fun create(webSiteCo: WebSiteCo) {

        val state = webSiteDao.create(webSiteCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getAllWebSite())
    }

    override fun update(webSiteUo: WebSiteUo) {

//        val hasWebSite = webSiteDao.get(webSiteUo.id)

        val state = webSiteDao.update(webSiteUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getAllWebSite())
    }

    override fun match(url: String): Int {
        return this.all().first { url.contains(it.domain) }.clientId
    }
}