package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo

interface BannerService {

    fun all(clientId: Int): List<Banner>

    fun create(bannerCo: BannerCo)

    fun update(bannerUo: BannerUo)

}