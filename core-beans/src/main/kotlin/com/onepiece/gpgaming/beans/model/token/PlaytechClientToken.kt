package com.onepiece.gpgaming.beans.model.token

class PlaytechClientToken(
        val apiPath: String,

        val loginPath: String,

        val accessToken: String,

        val admin: String,

        val agentName: String,

        val prefix: String,

        val serverName: String,

        val currency: String

) : ClientToken