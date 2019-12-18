package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.RecommendedType
import com.onepiece.treasure.beans.enums.Status

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