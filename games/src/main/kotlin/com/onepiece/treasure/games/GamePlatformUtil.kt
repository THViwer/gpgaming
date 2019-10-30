package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import org.springframework.stereotype.Component

@Component
class GamePlatformUtil(
        private val jokerPlatformCommon: PlatformCommon
)  {

    fun getPlatformBuild(platform: Platform): PlatformCommon {
        return when (platform) {
            Platform.Joker -> jokerPlatformCommon
            else -> jokerPlatformCommon
        }

    }
}

interface PlatformCommon {

    val gameApi: GameApi

    val gameCashApi: GameCashApi

    val gameOrderApi: GameOrderApi

}

@Component
class JokerPlatformCommon(
        val jokerGameApi: GameApi,
        val jokerGameCashApi: GameCashApi,
        val jokerGameOrderApi: GameOrderApi
) : PlatformCommon {

    override val gameApi: GameApi
        get() = jokerGameApi

    override val gameCashApi: GameCashApi
        get() = jokerGameCashApi

    override val gameOrderApi: GameOrderApi
        get() = jokerGameOrderApi
}
