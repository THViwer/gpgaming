package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.model.Contact
import io.swagger.annotations.ApiModelProperty

data class Contacts(

        @ApiModelProperty("微信")
        val wechatContact: Contact?,

        @ApiModelProperty("whatsapp")
        val whatsappContact: Contact?

)