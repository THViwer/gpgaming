package com.onepiece.gpgaming.su.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

sealed class PlatformBindSuValue {

    data class DefaultLogo(
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
            val platformDetailIconOver: String?
    )

    data class PlatformBindVo(

            @ApiModelProperty("id")
            val id: Int,

            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("厅主Id")
            val clientId: Int,

            @ApiModelProperty("平台后台登陆地址")
            val backUrl: String,

            @ApiModelProperty("是否开通")
            val open: Boolean,

            @ApiModelProperty("token_json")
            val tokenJson: String,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("密码")
            val password: String,

            @ApiModelProperty("状态")
            val status: Status,


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

            // 横面大图标
            val unclejayMobileIcon: String?,

            // 手机图标
            val mobileIcon: String,

            // 手机平台维护图标
            val mobileDisableIcon: String?,

            // 平台详细图标
            val platformDetailIcon: String?,

            // 平台详情鼠标移动图片
            val platformDetailIconOver: String?

    ) {
        @ApiModelProperty("平台类型")
        val category: PlatformCategory = platform.category

    }

    data class PlatformBindCoReq(

            @ApiModelProperty("厅主Id")
            val clientId: Int,

            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("token信息")
            val tokenJson: String,

            @ApiModelProperty("是否开通")
            val open: Boolean,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("密码")
            val password: String,


            // 平台名称
            val name: String,

            // 图标
            val icon: String,

            // unclejay大图标
            val unclejayMobleIcon : String?,

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
            val platformDetailIconOver: String?
    )


    data class PlatformBindUoReq(
            @ApiModelProperty("id")
            val id: Int,

            @ApiModelProperty("token信息")
            val tokenJson: String,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal?,

            @ApiModelProperty("用户名")
            val username: String?,

            @ApiModelProperty("密码")
            val password: String?,

            @ApiModelProperty("状态")
            val status: Status?,


            // 平台名称
            val name: String,

            // 图标
            val icon: String,

            val unclejayMobleIcon : String?,

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
            val platformDetailIconOver: String?
    )
}