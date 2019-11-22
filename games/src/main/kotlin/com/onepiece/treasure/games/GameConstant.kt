package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode

object GameConstant  {

    const val demoPath = "http://94.237.64.70"
    const val proxy = "http://94.237.64.70:90"

    // joker
//    const val JOKER_URL = "http://api688.net:81"
    const val JOKER_URL = "${demoPath}:81"
    const val JOKER_GAME_URL = "${demoPath}/iframe.html"

    // ct
//    const val CT_API_URL = "http://api.ctapi888.com"
    const val CT_API_URL = "${demoPath}:82"

    //    const val DG_API_URL = "http://api.dg99web.com"
//    const val DG_API_URL = "${demoPath}:89"
//    const val DG_API_URL = "${demoPath}:90/dg"

    // 918kiss
//    const val KISS918_API_URL = "http://api.918kiss.com:9991"
    const val KISS918_API_URL = "${demoPath}:83"
    //    const val KISS918_API_ORDER_URL = "http://api.918kiss.com:9919"
    const val KISS918_API_ORDER_URL = "${demoPath}:84"

    // sbo
//    const val SBO_API_URL = "sboapi.gsoft168.com"
    const val SBO_API_URL = "${demoPath}:85"
    const val SBO_START_URL = "http://sports-567gaming.568win.com/welcome2.aspx"

    // evolution
//    const val EVOLUTION_API_URL = "http://staging.evolution.asia-live.com"
    const val EVOLUTION_API_URL = "${demoPath}:94"

    //GoldDeluxe
//    const val GOLDDELUXE_API_URL = "http://api.coldsstaging.stack4kids.com/release/www/merchantapi.php"
    const val GOLDDELUXE_API_URL = "${demoPath}:87"

    // meaga
//    const val MEGA_API_URL = "http://mgt3.36ozhushou.com/mega-cloud/api/"
    const val MEGA_API_URL = "$demoPath:90/mega"

    // pussy888
    //    const val PUSSY888_API_URL = "http://api.pussy888.com"
    const val PUSSY_API_URL = "${demoPath}:92"
    //    const val PUSSY888_API_ORDER_URL = "http://api2.pussy888.com"
    const val PUSSY_API_ORDER_URL = "${demoPath}:93"

    // lbc
//    const val LBC_API_URL = "http://api.prod.ib.gsoft88.net"
    const val LBC_API_URL = "http://45.124.64.29:88" // hk server
    const val LBC_START_URL = "http://c.gsoft888.net/Deposit_ProcessLogin.aspx?lang=en&OType=1&WebSkinType=3&skincolor=bl001&g="
    const val LBC_START_MOBILE_URL = "http://i.gsoft888.net/Deposit_ProcessLogin.aspx?lang=en&OType=1&skincolor=bl001&ischinaview=True&st="

    // sexy game
//    const val SEXY_API_URL = "http://test.bikimex.com:10023"
    const val SEXY_API_URL = "${demoPath}:97"

    // fgg
//    const val FGG_API_URL = "https://d-tapi.fgg365.com"
    const val FGG_API_URL = "$proxy/fgg/Game"

    // bcs
//    const val BCS_API_URL = "https://transferapi.ballcrown.com"  // 测试地址
    const val BCS_API_URL = "${proxy}/bcs"

    //allbet
//    const val ALLBET_API_URL = "https://api3.apidemo.net:8443/"
    const val ALLBET_API_URL = "${proxy}/allbet"

    // ggFishing
    const val GGFISHING_API_URL = "https://optest.365gaming.cc:10029"

    fun getDomain(platform: Platform): String {
        when (platform) {
            Platform.GGFishing -> "https://optest.365gaming.cc:10029"
            Platform.DreamGaming -> "http://api.dg99web.com"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        //TODO 目前是测试
        return this.getDevDomain(platform)
    }

    fun getDevDomain(platform: Platform): String {
        return  when (platform) {
            Platform.GGFishing -> "${proxy}/gg"
            Platform.DreamGaming -> "${proxy}/dreamGaming"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }



}