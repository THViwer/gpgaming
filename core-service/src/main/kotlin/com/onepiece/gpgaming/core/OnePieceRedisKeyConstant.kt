package com.onepiece.gpgaming.core

import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform

object OnePieceRedisKeyConstant {

    fun getPlatforms() = "platform:all"

    fun getClient(clientId: Int) = "client:${clientId}"

    fun member(id: Int) = "member:$id"

    fun level(clientId: Int) = "level:$clientId"

    fun webSite(clientId: Int) = "webSite:$clientId"

    fun getAllWebSite() = "webSite:all"

    fun slotGames(platform: Platform) = "slotGames:$platform"

    fun clientBanks(clientId: Int) = "clientBank:$clientId"

    fun openPlatform(clientId: Int, platform: Platform) = "platform:open:$clientId:$platform"

    fun openPlatforms(clientId: Int) = "platforms:open:$clientId"

    fun myPlatformMembers(memberId: Int) = "platform:member:$memberId"

    fun betCache(unionId: String) = "bet:cache:$unionId"

    fun jokerNextId() = "joker:nextId"

    fun golDeluxeNextId() = "goldDeluxe:nextId"

    fun lastAnnouncement(clientId: Int) = "announcement:last:$clientId"

    fun banners(clientId: Int) = "banner:$clientId"

    fun promotions(clientId: Int) = "promotion:$clientId"

    fun slotGames(platform: Platform, launch: LaunchMethod) = "slot:games:$platform:$launch"

    fun pullBetOrderLastKey(clientId: Int, platform: Platform) = "pull:bet:$clientId:$platform"

    fun getLastMarkBetId(betRuleTable: String) = "mark:$betRuleTable"

    fun getMicroGameToken(username: String) = "mg:token:$username"

    fun getAllAppDown() = "app:down:all"

    fun getHotGames(clientId: Int) = "c:$clientId:hotGames"

    fun getSeo(clientId: Int) = "seo:${clientId}"

    fun getPayBinds(clientId: Int) = "pay:bind:$clientId"
}