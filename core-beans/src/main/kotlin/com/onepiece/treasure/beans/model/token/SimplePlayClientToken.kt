package com.onepiece.treasure.beans.model.token

class SimplePlayClientToken (

        val secretKey: String,

        val md5Key: String,

        val encryptKey: String,

        val saAppEncryptKey: String,

        val currency: String

): ClientToken