package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Banner
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface BannerDao: BasicDao<Banner> {

    fun create(bannerCo: BannerCo): Int

    fun update(bannerUo: BannerUo): Boolean

}