package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Banner
import com.onepiece.gpgaming.beans.value.database.BannerCo
import com.onepiece.gpgaming.beans.value.database.BannerUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface BannerDao: BasicDao<Banner> {

    fun create(bannerCo: BannerCo): Int

    fun update(bannerUo: BannerUo): Boolean

}