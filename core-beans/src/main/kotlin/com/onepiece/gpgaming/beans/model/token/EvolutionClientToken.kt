package com.onepiece.gpgaming.beans.model.token

class EvolutionClientToken(

        val apiPath: String,

        val apiOrderPath: String,

        val appId: String,

        val key: String,

        val username: String,

        val password: String,

        val currency: String,

        val country: String,

        val betLimit: String

) : ClientToken
