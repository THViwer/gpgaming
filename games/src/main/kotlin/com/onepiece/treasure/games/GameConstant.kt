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

            // slot
            Platform.Joker -> "http://api688.net:81"
            Platform.Kiss918 -> "http://api.918kiss.com:9991"
            Platform.Pussy888 -> "http://api.pussy888.com"
            Platform.Mega -> "http://mgt3.36ozhushou.com/mega-cloud"
            Platform.Pragmatic -> " https://api.prerelease-env.biz"
            Platform.SpadeGaming -> "http://api-egame-staging.sgplay.net"
            Platform.TTG -> "https://ams-api.stg.ttms.co:8443"

            // live
            Platform.CT -> "http://api.ctapi888.com"
            Platform.DreamGaming -> "http://api.dg99web.com"
            Platform.Evolution -> "http://staging.evolution.asia-live.com"
            Platform.GoldDeluxe -> "http://api.coldsstaging.stack4kids.com"
            Platform.SexyGaming -> "https://testapi.onlinegames22.com"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
            Platform.AllBet -> "https://api3.apidemo.net:8443"

            // sport
            Platform.Lbc -> "http://tsa.gpgaming88.com"
            Platform.Bcs -> "https://transferapi.ballcrown.com"
//            Platform.Sbo -> "http://sboapi.gsoft168.com"

            // fishing
            Platform.GGFishing -> "https://optest.365gaming.cc:10029"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    private fun getDevDomain(platform: Platform): String {

        val domain = "http://94.237.64.70"
        return  when (platform) {
            Platform.Lbc -> "http://45.124.64.29:88"
//            Platform.Pragmatic -> "https://api.prerelease-env.biz"

            // slot
            Platform.Joker -> "$domain:1001"
            Platform.Kiss918 -> "$domain:1002"
            Platform.Pussy888 -> "$domain:1003"
            Platform.Mega -> "$domain:1004"
            Platform.Pragmatic -> "$domain:1005"
            Platform.SpadeGaming -> "$domain:1006"
            Platform.TTG -> "$domain:1007"

            // live
            Platform.CT -> "$domain:2001"
            Platform.DreamGaming -> "$domain:2002"
            Platform.Evolution -> "$domain:2003"
            Platform.GoldDeluxe -> "$domain:2004"
            Platform.SexyGaming -> "$domain:2005"
            Platform.Fgg -> "$domain:2006"
            Platform.AllBet -> "$domain:2007"

            // sport
            Platform.Bcs -> "$domain:3001"

            // fishing
            Platform.GGFishing -> "$domain:4001"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    fun getOrderApiUrl(platform: Platform): String {
        val domain = "http://94.237.64.70"
        return when {
            platform == Platform.Pussy888 && activeConfig.profile == "dev" -> "$domain:7001"
            platform == Platform.Pussy888 -> "http://api2.pussy888.com"
            platform == Platform.Kiss918 && activeConfig.profile == "dev" -> "$domain:7002"
            platform == Platform.Kiss918 -> "http://api.918kiss.com:9919"
            platform == Platform.TTG && activeConfig.profile == "dev" -> "$domain:7003"
            platform == Platform.TTG -> "https://ams-df.stg.ttms.co:7443"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}