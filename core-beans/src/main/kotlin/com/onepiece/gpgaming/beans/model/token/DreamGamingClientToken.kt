package com.onepiece.gpgaming.beans.model.token

data class DreamGamingClientToken(
        val apiPath: String,
        val agentName: String,

        val key: String,

        val currency: String
): ClientToken