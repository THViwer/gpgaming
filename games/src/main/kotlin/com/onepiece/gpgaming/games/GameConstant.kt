package com.onepiece.gpgaming.games

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.core.ActiveConfig
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
            Platform.Joker -> "http://www.gwc688.net"
            Platform.Kiss918 -> "http://api.918kiss.com:9991"
            Platform.Pussy888 -> "http://api.pussy888.com"
            Platform.Mega -> "http://mgt3.36ozhushou.com"
            Platform.Pragmatic -> "https://api.prerelease-env.biz"
            Platform.SpadeGaming -> "http://api-egame-staging.sgplay.net"
            Platform.TTG -> "https://ams-api.stg.ttms.co:8443"
            Platform.MicroGaming -> "https://api.adminserv88.com"
//            Platform.GamePlay -> "http://club8api.bet8uat.com"
            Platform.GamePlay -> "http://18.163.131.134:1001"
            Platform.SimplePlay -> " http://api.sp-portal.com/api/api.aspx"

            // live
            Platform.CT -> "http://api.ctapi888.com"
            Platform.DreamGaming -> "http://api.dg99web.com"
            Platform.Evolution -> "http://staging.evolution.asia-live.com"
            Platform.GoldDeluxe -> "http://wsgd.gdsecure88.com"
            Platform.SexyGaming -> "https://testapi.onlinegames22.com"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
            Platform.AllBet -> "https://api3.apidemo.net:8443"
            Platform.EBet -> "http://gpgaming88myr.ebet.im:8888/api/"
            Platform.SaGaming -> "http://sai-api.sa-apisvr.com"

            // sport
//            Platform.Lbc -> "http://tsa.gpgaming88.com"
            Platform.Bcs -> "https://transferapi.ballcrown.com"
            Platform.CMD -> "https://api.fts368.com"
//            Platform.Sbo -> "http://sboapi.gsoft168.com"

            // fishing.
            Platform.GGFishing -> "https://yl368.ylgaming.net"

            Platform.PlaytechSlot, Platform.PlaytechLive -> "https://api-uat.gamzo.com"

            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    private fun getDevDomain(platform: Platform): String {

        val domain = "http://94.237.64.70"
        return  when (platform) {
            Platform.Joker -> "http://45.124.65.170:89"
            Platform.Pragmatic -> "https://api.prerelease-env.biz"
            Platform.Fgg -> "https://d-tapi.fgg365.com"
//            Platform.Lbc -> "http://45.124.65.170:88"
            Platform.Bcs -> "https://transferapi.ballcrown.com"

            // slot
            Platform.Kiss918 -> "$domain:1002"
            Platform.Pussy888 -> "$domain:1003"
            Platform.Mega -> "$domain:1004"
            Platform.SpadeGaming -> "$domain:1006"
            Platform.TTG -> "$domain:1007"
            Platform.MicroGaming -> "$domain:1008"
            Platform.GamePlay -> "http://45.124.65.170:90"
            Platform.SimplePlay -> "$domain:1011"

            // live
            Platform.CT -> "$domain:2001"
            Platform.DreamGaming -> "$domain:2002"
            Platform.Evolution -> "$domain:2003"
            Platform.GoldDeluxe -> "$domain:2004"
            Platform.SexyGaming -> "$domain:2005"
            Platform.AllBet -> "$domain:2007"
            Platform.SaGaming -> "$domain:2008"
            Platform.EBet -> "$domain:2010"

            // sport
//            Platform.CMD -> "$domain:3002"
            Platform.CMD -> "http://api.1win888.net"

            // fishing
            Platform.GGFishing -> "$domain:4001"

            Platform.PlaytechSlot, Platform.PlaytechLive -> "https://api-uat.gamzo.com"

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
            platform == Platform.GamePlay && activeConfig.profile == "dev" -> "http://45.124.65.170:91"
            platform == Platform.GamePlay -> "http://casino.w88uat.com"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}