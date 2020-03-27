package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.SlotSupport
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 平台gameId规则：
 * GamePlay: gameId = blossomgarden csv: /Users/cabbage/Desktop/gameplay_done.csv
 * Joker: gameId = 79mafnrjt48aa
 * Png: gameId = reactoonz
 * ttg: gameId = 1083:MadMonkey2:0 json = https://s3.ap-southeast-1.amazonaws.com/awspg1/slot/ttg_wap_en.json
 *
 */
data class SlotGame(

        // id
        val id: Int,

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

        // 状态
        val status: Status,

        // 排序字段
        val sequence: Int = 100,

        // 创建时间
        val createdTime: LocalDateTime
)