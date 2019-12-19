package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.model.Banner
import com.onepiece.gpgaming.beans.value.database.BannerCo
import com.onepiece.gpgaming.beans.value.database.BannerUo

interface BannerService {

    fun all(clientId: Int): List<Banner>

    fun findByType(clientId: Int, type: BannerType): List<Banner>

    fun create(bannerCo: BannerCo): Int

    fun update(bannerUo: BannerUo)

}