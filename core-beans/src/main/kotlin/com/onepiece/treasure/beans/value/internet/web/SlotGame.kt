package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty


data class SlotGame(

        @ApiModelProperty("游戏平台")
        val platform: Platform,

        @ApiModelProperty("游戏Id")
        val gameId: String,

        @ApiModelProperty("游戏类目")
        val category: GameCategory,

        @ApiModelProperty("游戏名称")
        val gameName: String,

        @ApiModelProperty("游戏中文名称")
        val chineseGameName: String,

        @ApiModelProperty("游戏图标")
        val icon: String,

        @ApiModelProperty("鼠标移上去的图标")
        val touchIcon: String?,

        @ApiModelProperty("热门")
        val hot: Boolean,

        @ApiModelProperty("新")
        val new: Boolean,

        @ApiModelProperty("状态")
        val status: Status

)