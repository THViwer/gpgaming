package com.onepiece.gpgaming.su.controller.value

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

sealed class ClientSuValue {

    data class ClientVo(

            @ApiModelProperty("厅主Id")
            val id: Int,

            @ApiModelProperty("bossId")
            val bossId: Int,

            @ApiModelProperty("国家")
            val country: Country,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("logo")
            val logo: String,

            @ApiModelProperty("tab logo")
            val shortcutLogo: String,

            @ApiModelProperty("昵称")
            val name: String,

            @ApiModelProperty("平台开通数量")
            val openNumber: Int,

            @ApiModelProperty("状态")
            val status: Status,

            @ApiModelProperty("创建时间")
            val createdTime: LocalDateTime,

            @ApiModelProperty("ip白名单s")
            val whitelists: List<String>

    )

    data class ClientCoReq(

            @ApiModelProperty("bossId")
            val bossId: Int,

            @ApiModelProperty("国家")
            val country: Country,

            @ApiModelProperty("用户名")
            val username: String,

            @ApiModelProperty("logo")
            val logo: String,

            @ApiModelProperty("tab logo")
            val shortcutLogo: String,

            @ApiModelProperty("昵称")
            val name: String,

            @ApiModelProperty("密码")
            val password: String,

            @ApiModelProperty("ip白名单")
            val whitelists: List<String>

    )

    data class ClientUoReq(

            @ApiModelProperty("用户Id")
            val id: Int,

            @ApiModelProperty("昵称")
            val name: String?,

            @ApiModelProperty("logo")
            val logo: String?,

            @ApiModelProperty("tab logo")
            val shortcutLogo: String?,

            @ApiModelProperty("用户密码")
            val password: String?,

            @ApiModelProperty("状态")
            val status: Status?,

            @ApiModelProperty("ip白名单")
            val whitelists: List<String>?

    )

    data class WebSiteCo(

            val clientId: Int,

            val domain: String
    )

    data class WebSiteUo(
            val id: Int,

            val domain: String
    )


}