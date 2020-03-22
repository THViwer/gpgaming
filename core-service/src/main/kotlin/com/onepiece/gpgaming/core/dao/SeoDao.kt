package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface SeoDao: BasicDao<Seo> {

    fun create(seoUo: SeoValue.SeoUo): Boolean

    fun update(seoUo: SeoValue.SeoUo): Boolean

}