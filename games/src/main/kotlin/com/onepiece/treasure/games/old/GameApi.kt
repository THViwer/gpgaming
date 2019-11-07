package com.onepiece.treasure.games.old

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.exceptions.LogicException
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.games.value.SlotGame

abstract class GameApi {

    abstract  fun register(clientAuthVo: ClientAuthVo? = null, username: String, password: String): String

    open fun games(): List<SlotGame> {
        throw LogicException(OnePieceExceptionCode.AUTHORITY_FAIL)
    }


    open fun start(clientAuthVo: ClientAuthVo? = null, username: String, gameId: String, redirectUrl: String): Map<StartPlatform, String> {
        throw LogicException(OnePieceExceptionCode.AUTHORITY_FAIL)
    }

    open fun start(clientAuthVo: ClientAuthVo? = null, username: String, password: String): Map<StartPlatform, String> {
        throw LogicException(OnePieceExceptionCode.AUTHORITY_FAIL)
    }

}

