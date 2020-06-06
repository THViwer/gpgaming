package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import springfox.documentation.annotations.ApiIgnore

sealed class BlogValue {

    data class BlogCo(

            // 代理Id
            @ApiIgnore
            val clientId: Int,

            // 标题
            val title: String,

            // 图片
            val headImg: String,

            // 排序
            val sort: Int,

            // 平台
            val platform: Platform
    )

    data class BlogUo(

            // id
            val id: Int,

            // 标题
            val title: String,

            // 图片
            val headImg: String,

            // 排序
            val sort: Int,

            // 平台
            val platform: Platform,

            // 状态
            val status: Status
    )

    data class BlogMVo(

            // id
            val id: Int,

            // 标题
            val title: String,

            // 图片
            val headImg: String,

            // 排序
            val sort: Int,

            // 平台
            val platform: Platform,

            // 国际化内容
            val content: I18nContent,

            // 状态
            val status: Status
    )

    data class BlogVo(

            // id
            val id: Int,

            // 标题
            val title: String,

            // 图片
            val headImg: String,

            // 排序
            val sort: Int,

            // 平台
            val platform: Platform,

            // 国际化内容
            val contents: List<I18nContent>,

            // 状态
            val status: Status
    )


}