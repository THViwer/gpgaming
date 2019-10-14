package com.onepiece.treasure.account.model

import com.onepiece.treasure.account.model.enums.Status

/**
 * 会员等级
 */
data class Level(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 名称
        val name: String,

        // 状态
        val status: Status
)