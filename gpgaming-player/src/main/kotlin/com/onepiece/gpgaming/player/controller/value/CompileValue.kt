package com.onepiece.gpgaming.player.controller.value

sealed class CompileValue {


    data class Config(

            val bossId: Int,

            val clientId: Int,

            val logo: String,

            val shortcutLogo: String

    )

    data class AffSite(
            val path: String?
    )


}