package com.onepiece.gpgaming.beans.model.token

data class GamePlayClientToken(
        val apiPath: String,

        val apiOrderPath: String,

        val merchId: String,

        val merchPwd: String,

        val currency: String

): ClientToken