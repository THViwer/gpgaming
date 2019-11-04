package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import org.springframework.stereotype.Component

@Component
class GamePlatformUtil(
        private val jokerPlatformCommon: PlatformCommon,
        private val cta666PlatformCommon: Cta666PlatformCommon
)  {

    fun getPlatformBuild(platform: Platform): PlatformCommon {
        return when (platform) {
            Platform.Joker -> jokerPlatformCommon
            Platform.Cta666 -> cta666PlatformCommon
            else -> error("")
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


@Component
class Cta666PlatformCommon(
        val cta666GameApi: GameApi,
        val cta666GameCashApi: GameCashApi,
        val cta666GameOrderApi: GameOrderApi
) : PlatformCommon {

    override val gameApi: GameApi
        get() = cta666GameApi

    override val gameCashApi: GameCashApi
        get() = cta666GameCashApi

    override val gameOrderApi: GameOrderApi
        get() = cta666GameOrderApi
}
