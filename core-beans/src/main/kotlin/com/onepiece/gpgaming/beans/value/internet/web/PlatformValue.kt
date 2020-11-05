package com.onepiece.gpgaming.beans.value.internet.web

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.PlatformBind
import io.swagger.annotations.ApiModelProperty

object PlatformValueFactory {
//
//    fun generatorPlatforms(): List<PlatformVo> {
//
//        val p1 = PlatformVo(id = 1, name = "AG-老虎机", category = PlatformCategory.Slot, status = true, open = true)
//        val p2 = p1.copy(id = -1, category = PlatformCategory.Fishing, name = "SUN-捕鱼", open = false)
//        val p3 = p1.copy(id = 2, category = PlatformCategory.LiveVideo, name = "X-真人")
//        val p4 = p1.copy(id = 3, category = PlatformCategory.Sport, name = "X-体育")
//
//        return listOf(p1, p2, p3, p4)
//    }

}

sealed class PlatformValue {

        data class PlatformBindUo(

                // id
                val id: Int,

                // 是否热门
                val hot: Boolean?,

                // 是否最新
                val new: Boolean?,


                // 平台名称
                val name: String,

                // 图标
                val icon: String,

                // 平台维护图标
                val disableIcon: String?,

                // 原始图标
                val originIcon: String,

                // 原始鼠标移上去图标
                val originIconOver: String,

                // 手机图标
                val mobileIcon: String,

                // 手机平台维护图标
                val mobileDisableIcon: String?,

                // 平台详细图标
                val platformDetailIcon: String?,

                // 平台详情鼠标移动图片
                val platformDetailIconOver: String?,

                val unclejayMobileIcon: String?
        )


}

data class PlatformVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("平台")
        val platform: Platform,

//        @JsonIgnore
//        val gamePlatform: GamePlatform,

        @JsonIgnore
        val platformBind: PlatformBind,

        @ApiModelProperty("是否热门")
        val hot: Boolean,

        @ApiModelProperty("是否最新")
        val new: Boolean,

        @ApiModelProperty("是否启用")
        val status: Status,

        @ApiModelProperty("是否开通")
        val open: Boolean

) {

        val category: PlatformCategory
                @ApiModelProperty("平台类型")
                get() = platform.category

        val logo: String
                @ApiModelProperty("平台logo")
                get() = platformBind.icon

        val name: String
                @ApiModelProperty("平台名称")
                get() = platformBind.name

        // 图标
        val icon: String = platformBind.icon

        // 平台维护图标
        val disableIcon: String? = platformBind.disableIcon

        // 原始图标
        val originIcon: String = platformBind.originIcon

        // 原始鼠标移上去图标
        val originIconOver: String = platformBind.originIconOver

        // 手机图标
        val mobileIcon: String = platformBind.mobileIcon

        // 手机平台维护图标
        val mobileDisableIcon: String? = platformBind.mobileDisableIcon

        // 平台详细图标
        val platformDetailIcon: String? = platformBind.platformDetailIcon

        // 平台详情鼠标移动图片
        val platformDetailIconOver: String? = platformBind.platformDetailIconOver

        val unclejayMobileIcon: String? = platformBind.unclejayMobileIcon
}


data class PlatformUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("是否启用")
        val status: Status
)
