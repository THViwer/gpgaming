package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.model.Contact
import io.swagger.annotations.ApiModelProperty

data class Contacts(

        @ApiModelProperty("微信")
        val wechatContact: Contact?,

        @ApiModelProperty("whatsapp")
        val whatsappContact: Contact?,

        @ApiModelProperty("facebook")
        val facebook: Contact?,

        @ApiModelProperty("youTuBe")
        val youtube: Contact?,

        @ApiModelProperty("instagram")
        val instagram: Contact?

)