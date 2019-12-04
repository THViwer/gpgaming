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
            Platform.Joker -> "http://api688.net"
            Platform.Kiss918 -> "http://api.918kiss.com:9991"
            Platform.Pussy888 -> "http://api.pussy888.com"
            Platform.Mega -> "http://mgt3.36ozhushou.com/mega-cloud"
            Platform.Pragmatic -> "https://api.prerelease-env.biz"
            Platform.SpadeGaming -> "http://api-egame-staging.sgplay.net"
            Platform.TTG -> "https://ams-api.stg.ttms.co:8443"
            Platform.MicroGaming -> "https://api.adminserv88.com"
            Platform.PlaytechSlot -> "https://api-uat.gamzo.com"

            // live
            Platform.CT -> "http://api.ctapi888.com"
            Platform.DreamGaming -> "http://api.dg99web.com"
            Platform.Evolution -> "http://staging.evolution.asia-live.com"
            Platform.GoldDeluxe -> "http://wsgd.gdsecure88.com"
            Platform.SexyGaming -> "https://testapi.onlinegames22.com"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
            Platform.AllBet -> "https://api3.apidemo.net:8443"

            // sport
//            Platform.Lbc -> "http://tsa.gpgaming88.com"
            Platform.Bcs -> "https://transferapi.ballcrown.com"
            Platform.CMD -> "http://api.1win88.net:8080"
//            Platform.Sbo -> "http://sboapi.gsoft168.com"

            // fishing
            Platform.GGFishing -> "https://optest.365gaming.cc:10029"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    private fun getDevDomain(platform: Platform): String {

        val domain = "http://94.237.64.70"
        return  when (platform) {
            Platform.Joker -> "http://45.124.64.29:89"
            Platform.Pragmatic -> "https://api.prerelease-env.biz"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
//            Platform.Lbc -> "http://45.124.64.29:88"
            Platform.Bcs -> "https://transferapi.ballcrown.com"

            // slot
            Platform.Kiss918 -> "$domain:1002"
            Platform.Pussy888 -> "$domain:1003"
            Platform.Mega -> "$domain:1004"
            Platform.SpadeGaming -> "$domain:1006"
            Platform.TTG -> "$domain:1007"
            Platform.MicroGaming -> "$domain:1008"
            Platform.PlaytechSlot -> "https://api-uat.gamzo.com" // 香港 CN2 01 - 443 单端口

            // live
            Platform.CT -> "$domain:2001"
            Platform.DreamGaming -> "$domain:2002"
            Platform.Evolution -> "$domain:2003"
            Platform.GoldDeluxe -> "$domain:2004"
            Platform.SexyGaming -> "$domain:2005"
            Platform.AllBet -> "$domain:2007"

            // sport
            Platform.CMD -> "$domain:3002"

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
            platform == Platform.GoldDeluxe && activeConfig.profile == "dev" -> "$domain:7004"
            platform == Platform.GoldDeluxe -> "http://wsgdreport.gdsecure88.com"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}