package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.AdvertType
import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.PlatformCategory
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


data class ConfigVo(

        @ApiModelProperty("公告")
        val announcementVo: AnnouncementVo?,

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>,

        @ApiModelProperty("广告列表")
        val adverts: List<AdvertVo>

)

data class AnnouncementVo(

        @ApiModelProperty("标题")
        val title: String,

        @ApiModelProperty("内容")
        val content: String,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime
)

data class AdvertVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("排序")
        val order: Int,

        @ApiModelProperty("图标")
        val icon: String,

        @ApiModelProperty("鼠标移动上去图标")
        val touchIcon: String?,

        @ApiModelProperty("方位")
        val position: AdvertType,

        @ApiModelProperty("连接地址")
        val link: String
)



data class PlatformVo(

        @ApiModelProperty("平台Id")
        val id: Int,

        @ApiModelProperty("名称")
        val name: String,

        @ApiModelProperty("类目")
        val category: PlatformCategory,

        @ApiModelProperty("平台状态")
        val status: Status
)

data class SlotMenu(

        @ApiModelProperty("游戏Id")
        val gameId: String,

        @ApiModelProperty("游戏类目")
        val category: GameCategory,

        @ApiModelProperty("游戏名称")
        val gameName: String,

        @ApiModelProperty("游戏图标")
        val icon: String,

        @ApiModelProperty("热门")
        val hot: Boolean,

        @ApiModelProperty("新")
        val new: Boolean,

        @ApiModelProperty("状态")
        val status: Status

)

data class StartGameResp(

        @ApiModelProperty("平台地址")
        val path: String
)
