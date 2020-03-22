package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue


interface SeoService {

    fun get(clientId: Int): Seo

    fun update(seoUo: SeoValue.SeoUo)

}