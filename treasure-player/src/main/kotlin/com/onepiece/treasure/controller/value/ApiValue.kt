package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.PlatformCategory
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty

//object ApiValueFactory {
//
//    fun generatorConfig(): ConfigVo {
//
//        val p1 = PlatformVo(id = 1, name = "捕鱼", category = PlatformCategory.Fishing, hot = true, new = false, status = Status.Normal)
//        val p2 = p1.copy(id = 2, name = "老虎吃人", category = PlatformCategory.Slot, hot = false, new = true)
//        val p3 = p1.copy(id = 3, name = "色播", category = PlatformCategory.LiveVideo, hot = false, new = false)
//        val p4 = p1.copy(id = 4, name = "欧杯", category = PlatformCategory.Sport, hot = true, new = true, status = Status.Stop)
//        val p5 = p1.copy(id = 5, name = "六合彩", category = PlatformCategory.Lottery, hot = true, new = false)
//
//        val platforms = listOf(p1, p2, p3, p4, p5)
//
//        return ConfigVo(platforms = platforms)
//    }
//
//    fun generatorGameResp(): StartGameResp {
//        return StartGameResp(id = 1, path = "http://www.baidu.com")
//    }
//
//    fun generatorSlotMenus(): List<SlotMenu> {
//
//        val icon = "http://i1.sinaimg.cn/ent/d/2008-06-04/U105P28T3D2048907F326DT20080604225106.jpg"
//
//        val m1 = SlotMenu(id = 1, category = GameCategory.SLOT, name = "老虎", icon = icon, hot = true, new = true, status = Status.Normal)
//        val m2 = m1.copy(id = 2, name = "青蛙", hot = true, new = false)
//        val m3 = m1.copy(id = 3, name = "大象", hot = false, new = false)
//        val m4 = m1.copy(id = 4, name = "蛇", hot = false, new = false, status = Status.Stop)
//        val m5 = m1.copy(id = 5, name = "老鼠", hot = false, new = false)
//
//        return listOf(m1, m2, m3, m4 ,m5)
//    }
//}

data class ConfigVo(

        @ApiModelProperty("平台列表")
        val platforms: List<PlatformVo>

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
