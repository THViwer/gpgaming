package com.onepiece.gpgaming.beans.model.token

class MicroGamingClientToken(
        val apiPath: String,

        val authUsername: String,

        val authPassword: String,

        val username: String,

        val password: String,

        val parentId: Int,

        val currency: String
) : ClientToken