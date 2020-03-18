package com.onepiece.gpgaming.beans.model.token

data class GamePlayClientToken(

        val apiPath: String,

        val gamePath: String,

        val mobileGamePath: String,

        val apiOrderPath: String,

        val merchId: String,

        val merchPwd: String,

        val currency: String

): ClientToken