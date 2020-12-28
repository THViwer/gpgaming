package com.onepiece.gpgaming.player.controller.value

sealed class SmsValue {

    data class PhoneMsgResponse(
            val phone: String
    )


}