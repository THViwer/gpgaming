package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.WebSite
import com.onepiece.treasure.beans.value.database.WebSiteCo
import com.onepiece.treasure.beans.value.database.WebSiteUo

interface WebSiteService {

    fun all(clientId: Int): List<WebSite>

    fun create(webSiteCo: WebSiteCo)

    fun update(webSiteUo: WebSiteUo)

}