package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 首页广告
 */
data class Banner(

        // id
        val id: Int,

        // 排序
        val order: Int,

        // 厅主Id
        val clientId: Int,

//        // 图标
//        val icon: String,
//
//        // 鼠标移动上去图标
//        val touchIcon: String?,

        // 位置
        val type: BannerType,

        // 當type=TrendingGames时 需要配置平台类型 当type=其它时 值为空
        val platformCategory: PlatformCategory?,

        // 连接地址
        val link: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 更新时间
        val updatedTime: LocalDateTime

)