package com.onepiece.treasure.beans.model.token

class MicroGamingClientToken(
        val authUsername: String,

        val authPassword: String,

        val username: String,

        val password: String,

        val parentId: Int
) : ClientToken