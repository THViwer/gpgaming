package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty


data class SlotCategoryVo(

        @ApiModelProperty("游戏分类")
        val gameCategory: GameCategory,

        @ApiModelProperty("游戏列表")
        val games: List<SlotGameVo>

)

data class SlotGameVo (

        @ApiModelProperty("平台")
        val platform: Platform,

        @ApiModelProperty("gameId")
        val gameId: String,

        @ApiModelProperty("类目")
        val category: GameCategory,

        @ApiModelProperty("游戏名称")
        val gameName: String,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("鼠标移动图标")
        val touchIcon: String?,

        @ApiModelProperty("是否热门")
        val hot: Boolean,

        @ApiModelProperty("是否是新游戏")
        val new: Boolean,

        @ApiModelProperty("状态")
        val status: Status

)