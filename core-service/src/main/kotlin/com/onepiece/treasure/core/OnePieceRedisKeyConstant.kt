package com.onepiece.treasure.core

object OnePieceRedisKeyConstant {

    fun member(id: Int) = "member:$id"

    fun level(clientId: Int) = "level:$clientId"

    fun webSite(clientId: Int) = "webSite:$clientId"

}