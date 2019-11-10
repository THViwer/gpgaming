package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import java.math.BigDecimal

abstract class PlatformApi {

    abstract fun register(registerReq: GameValue.RegisterReq): String

    abstract fun balance(balanceReq: GameValue.BalanceReq): BigDecimal

    abstract fun transfer(transferReq: GameValue.TransferReq): String

    open fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    open fun start(startReq: GameValue.StartReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    open fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    open fun startSlotDemo(token: ClientToken, startPlatform: StartPlatform): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    open fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    open fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }



}