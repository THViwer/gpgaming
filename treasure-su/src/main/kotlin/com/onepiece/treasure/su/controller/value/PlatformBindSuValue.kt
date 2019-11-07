package com.onepiece.treasure.su.controller.value

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

sealed class PlatformBindSuValue {

    data class PlatformBindVo(

            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("厅主Id")
            val clientId: Int,

            @ApiModelProperty("平台后台登陆地址")
            val backUrl: String,

            @ApiModelProperty("是否开通")
            val open: Boolean,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("密码")
            val password: String

    )

    data class PlatformBindCoReq(

            @ApiModelProperty("厅主Id")
            val clientId: Int,

            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("是否开通")
            val open: Boolean,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("密码")
            val password: String
    )


    data class PlatformBindUoReq(
            @ApiModelProperty("id")
            val id: Int,

            @ApiModelProperty("保证金")
            val earnestBalance: BigDecimal?,

            @ApiModelProperty("用户名")
            val username: String?,

            @ApiModelProperty("密码")
            val password: String?,

            @ApiModelProperty("状态")
            val status: Status?
    )
}