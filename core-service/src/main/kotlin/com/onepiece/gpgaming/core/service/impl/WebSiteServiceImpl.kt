package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
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

    override fun getDataByBossId(bossId: Int): List<WebSite> {
        return this.all().filter { it.bossId == bossId }
    }

    override fun getAffSite(clientId: Int): WebSite? {
        return this.getDataByBossId(bossId = -1).firstOrNull { it.clientId == clientId && it.status == Status.Normal && it.country == Country.Default }
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

    override fun match(url: String): WebSite {
//        val firstMatchUrl = url.removeSuffix("https://").removeSuffix("www.")
        val sites = this.all()

        val removeHttpUrl = url.removePrefix("https://").removeSuffix("/#/")
        val path = removeHttpUrl.substring(removeHttpUrl.indexOf(".") + 1, removeHttpUrl.length)

        return sites.firstOrNull { it.domain == path } ?: sites.first { url.contains(it.domain) }
    }

    override fun matchReturnBossId(url: String): Int {
        return this.match(url = url).bossId
    }
}
