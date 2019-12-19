package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status

sealed class RecommendedValue {

    data class CreateVo(

            // 厅主Id
            val clientId: Int,

            // 推荐类型
            val type: RecommendedType,

            // 内容 json格式
            val contentJson: String,

            // 状态
            val status: Status
    )

    data class UpdateVo(

            val id: Int,

            val clientId: Int,

            val contentJson: String,

            val status: Status
    )


}