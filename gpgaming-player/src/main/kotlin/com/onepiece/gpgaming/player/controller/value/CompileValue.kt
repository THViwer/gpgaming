package com.onepiece.gpgaming.player.controller.value

sealed class CompileValue {


    data class Config(

            val logo: String,

            val shortcutLogo: String

    )


}