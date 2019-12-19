package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.GameCategory
import io.swagger.annotations.ApiModelProperty

data class SlotCategory(
        @ApiModelProperty("类目名车")
        val gameCategory: GameCategory,

        @ApiModelProperty("游戏列表")
        val games: List<SlotGame>
)