package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Platform
import io.swagger.annotations.ApiModelProperty

sealed class AppDownWebValue {

    data class CoReq(
            @ApiModelProperty("平台")
            val platform: Platform,

            @ApiModelProperty("ios地址")
            val iosPath: String?,

            @ApiModelProperty("android地址")
            val androidPath: String?
    )

}