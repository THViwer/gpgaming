package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status

sealed class RecommendedWebValue {

    data class CreateReq(

            // 推荐类型
            val type: RecommendedType,

            // 内容 json格式
            val contentJson: String

    )

    data class UpdateReq(

            val id: Int,

            val contentJson: String,

            val status: Status
    )


}