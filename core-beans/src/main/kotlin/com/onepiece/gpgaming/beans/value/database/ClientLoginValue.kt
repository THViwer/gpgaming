package com.onepiece.gpgaming.beans.value.database


sealed class ClientLoginValue{

    data class ClientLoginReq(

            val clientId: Int,

            val username: String,

            val password: String,

            val ip: String
    )

}

