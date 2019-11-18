package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform

object OnePieceRedisKeyConstant {

    fun member(id: Int) = "member:$id"

    fun level(clientId: Int) = "level:$clientId"

    fun webSite(clientId: Int) = "webSite:$clientId"

    fun slotGames(platform: Platform) = "slotGames:$platform"

    fun clientBanks(clientId: Int) = "clientBank:$clientId"

    fun openPlatform(clientId: Int, platform: Platform) = "platform:open:$clientId:$platform"

    fun openPlatforms(clientId: Int) = "platforms:open:$clientId"

    fun myPlatformMembers(memberId: Int) = "platform:member:$memberId"

    fun betCache(unionId: String) = "bet:cache:$unionId"

    fun jokerNextId() = "joker:nextId"

    fun golDeluxeNextId() = "goldDeluxe:nextId"

    fun lastAnnouncement(clientId: Int) = "announcement:last:$clientId"

    fun adverts(clientId: Int) = "advert:$clientId"

    fun promotions(clientId: Int) = "promotion:$clientId"

    fun slotGames(platform: Platform, launch: LaunchMethod) = "slot:games:$platform:$launch"

    fun getLastMarkBetId(betRuleTable: String) = "mark:$betRuleTable"
}