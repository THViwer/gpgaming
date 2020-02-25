package com.onepiece.gpgaming.su.controller.value

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.token.ClientToken
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

sealed class PlatformBindSuValue {

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
            val status: Status

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
            val password: String
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
            val status: Status?
    )
}