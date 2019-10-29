package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.Platform

object OnePieceRedisKeyConstant {

    fun member(id: Int) = "member:$id"

    fun level(clientId: Int) = "level:$clientId"

    fun webSite(clientId: Int) = "webSite:$clientId"

    fun slotGames(platform: Platform) = "slotGames:$platform"

    fun clientBanks(clientId: Int) = "clientBank:$clientId"

    fun openPlatforms(clientId: Int) = "platforms:open:$clientId"

    fun myPlatformMembers(memberId: Int) = "platform:member:$memberId"

    fun betCache(unionId: String) = "bet:cache:$unionId"

    fun jokerNextId() = "joker:nextId"
}