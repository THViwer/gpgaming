package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.database.WebSiteCo
import com.onepiece.gpgaming.beans.value.database.WebSiteUo

interface WebSiteService {

    fun all(): List<WebSite>

    fun getDataByBossId(bossId: Int): List<WebSite>

    fun getAffSite(clientId: Int): WebSite?

    fun create(webSiteCo: WebSiteCo)

    fun update(webSiteUo: WebSiteUo)

    fun match(url: String): WebSite

    fun matchReturnBossId(url: String): Int
}