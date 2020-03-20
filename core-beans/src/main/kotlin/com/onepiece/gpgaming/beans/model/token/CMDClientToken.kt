package com.onepiece.gpgaming.beans.model.token

class CMDClientToken(

        val apiPath: String,

        val gamePath: String,

        val mobileGamePath: String,

        val partnerKey: String,

        val currency: String

) : ClientToken