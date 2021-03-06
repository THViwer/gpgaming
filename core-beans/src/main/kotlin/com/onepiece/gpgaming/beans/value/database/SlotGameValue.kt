package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.SlotSupport
import com.onepiece.gpgaming.beans.enums.Status

sealed class SlotGameValue {

    data class SlotGameCo(

            // 平台
            val platform: Platform,

            // 游戏分类
            val category: GameCategory,

            // 是否热门
            val hot: Boolean,

            // 是否新游戏
            val new: Boolean,

            // 游戏Id
            val gameId: String,

            // 支持类型
            val launchs: List<LaunchMethod>,

            // 中文名
            val cname: String,

            // 英文名
            val ename: String,

            // 中文logo
            val clogo: String,

            // 英文logo
            val elogo: String,

            // 序列
            val sequence: Int,

            // 状态
            val status: Status
    )


    data class SlotGameUo(

            // id
            val id: Int,

            // 平台
            val platform: Platform?,

            // 游戏分类
            val category: GameCategory?,

            // 是否热门
            val hot: Boolean?,

            // 是否新游戏
            val new: Boolean?,

            // 游戏Id
            val gameId: String?,

            // 支持类型
            val launchs: List<LaunchMethod>,

            // 中文名
            val cname: String?,

            // 英文名
            val ename: String?,

            // 中文logo
            val clogo: String?,

            // 英文logo
            val elogo: String?,

            // 序列
            val sequence: Int,

            // 状态
            val status: Status?
    )

}