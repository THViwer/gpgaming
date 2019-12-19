package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.database.WebSiteCo
import com.onepiece.gpgaming.beans.value.database.WebSiteUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface WebSiteDao: BasicDao<WebSite> {

    fun create(webSiteCo: WebSiteCo): Boolean

    fun update(webSiteUo: WebSiteUo): Boolean

}