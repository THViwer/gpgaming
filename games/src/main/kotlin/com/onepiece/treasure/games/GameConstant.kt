package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import org.springframework.stereotype.Component

@Component
class GameConstant(
        private val activeConfig: ActiveConfig
) {

    private val proxy = "http://94.237.64.70:90"
    // sbo
    // const val SBO_START_URL = "http://sports-567gaming.568win.com/welcome2.aspx"

    fun getDomain(platform: Platform): String {

        if (activeConfig.profile== "dev") {
            return this.getDevDomain(platform)
        }

        return when (platform) {
            Platform.GGFishing -> "https://optest.365gaming.cc:10029"
            Platform.DreamGaming -> "http://api.dg99web.com"
            Platform.Lbc -> "http://tsa.gpgaming88.com"
            Platform.Pragmatic -> " https://api.prerelease-env.biz"
            Platform.SpadeGaming -> "http://api-egame-staging.sgplay.net"
            Platform.SexyGaming -> ""
            Platform.AllBet -> "https://api3.apidemo.net:8443"
            Platform.Bcs -> "https://transferapi.ballcrown.com"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
            Platform.Pussy888 -> "http://api.pussy888.com"
            Platform.Mega -> "http://mgt3.36ozhushou.com/mega-cloud"
            Platform.GoldDeluxe -> "http://api.coldsstaging.stack4kids.com"
            Platform.Evolution -> "http://staging.evolution.asia-live.com"
//            Platform.Sbo -> "http://sboapi.gsoft168.com"
            Platform.Kiss918 -> "http://api.918kiss.com:9991"
            Platform.CT -> "http://api.ctapi888.com"
            Platform.Joker -> "http://api688.net:81"
            Platform.TTG -> "https://ams-api.stg.ttms.co:8443"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    private fun getDevDomain(platform: Platform): String {
        return  when (platform) {
            Platform.GGFishing -> "${proxy}/gg"
            Platform.DreamGaming -> "${proxy}/dreamGaming"
            Platform.Lbc -> "http://45.124.64.29:88"
            Platform.Pragmatic -> "https://api.prerelease-env.biz"
            Platform.SpadeGaming -> "${proxy}/spadeGaming"
            Platform.SexyGaming -> ""
            Platform.AllBet -> "${proxy}/allbet"
            Platform.Bcs -> "${proxy}/bcs"
            Platform.Fgg -> "${proxy}/fgg/Game"
            Platform.Pussy888 -> "${proxy}/pussy"
            Platform.Mega -> "${proxy}/mega"
            Platform.GoldDeluxe -> "$proxy/goldDeluxe"
            Platform.Evolution -> "${proxy}/evolution"
//            Platform.Sbo -> "${proxy}/sbo"
            Platform.Kiss918 -> "${proxy}/kiss918"
            Platform.CT -> "${proxy}/ct"
            Platform.Joker -> "${proxy}/joker"
//            Platform.TTG -> "${proxy}/ttg"
            Platform.TTG -> "http://94.237.64.70:106"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    fun getOrderApiUrl(platform: Platform): String {
        return when {
            platform == Platform.Pussy888 && activeConfig.profile == "dev" -> "$proxy/pussyOrder"
            platform == Platform.Pussy888 -> "http://api2.pussy888.com"
            platform == Platform.Kiss918 && activeConfig.profile == "dev" -> "$proxy/kiss918Order"
            platform == Platform.Kiss918 -> "http://api.918kiss.com:9919"
            platform == Platform.TTG && activeConfig.profile == "dev" -> "http://94.237.64.70:107"
            platform == Platform.TTG -> "https://ams-df.stg.ttms.co:7443"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}