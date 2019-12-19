package com.onepiece.gpgaming.games.value

data class ClientAuthVo (

    val username: String?,

    val password: String?
) {

    companion object {

        fun ofKiss918(username: String): ClientAuthVo {
            return ClientAuthVo(username = username, password = null)
        }

        fun empty(): ClientAuthVo {
            return ClientAuthVo(username = null, password = null)
        }

    }

}