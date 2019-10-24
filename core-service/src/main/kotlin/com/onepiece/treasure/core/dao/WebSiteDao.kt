package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.WebSiteCo
import com.onepiece.treasure.core.dao.value.WebSiteUo
import com.onepiece.treasure.core.model.WebSite

interface WebSiteDao: BasicDao<WebSite> {

    fun create(webSiteCo: WebSiteCo): Boolean

    fun update(webSiteUo: WebSiteUo): Boolean

}