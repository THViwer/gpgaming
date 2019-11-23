package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.BannerType
import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo

interface BannerService {

    fun all(clientId: Int): List<Banner>

    fun findByType(clientId: Int, type: BannerType): List<Banner>

    fun create(bannerCo: BannerCo)

    fun update(bannerUo: BannerUo)

}