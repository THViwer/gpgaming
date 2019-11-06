package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import org.springframework.stereotype.Component

@Component
class GamePlatformUtil(
        private val jokerPlatformCommon: PlatformCommon,
        private val cta666PlatformCommon: Cta666PlatformCommon,
        private val kiss918PlatformCommon: Kiss918PlatformCommon
)  {

    fun getPlatformBuild(platform: Platform): PlatformCommon {
        return when (platform) {
            Platform.Joker -> jokerPlatformCommon
            Platform.Kiss918 -> kiss918PlatformCommon

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

@Component
class Kiss918PlatformCommon(
        val kiss918GameApi: GameApi,
        val kiss918GameCashApi: GameCashApi,
        val kiss918GameOrderApi: GameOrderApi

) : PlatformCommon {

    override val gameApi: GameApi
        get() = kiss918GameApi
    override val gameCashApi: GameCashApi
        get() = kiss918GameCashApi
    override val gameOrderApi: GameOrderApi
        get() = kiss918GameOrderApi
}