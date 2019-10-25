package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.WebSiteCo
import com.onepiece.treasure.beans.value.database.WebSiteUo
import com.onepiece.treasure.beans.model.WebSite

interface WebSiteDao: BasicDao<WebSite> {

    fun create(webSiteCo: WebSiteCo): Boolean

    fun update(webSiteUo: WebSiteUo): Boolean

}