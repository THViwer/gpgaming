package com.onepiece.gpgaming.mr.selenium

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.mr.selenium.impl.DreamGamingSelenium
import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.openqa.selenium.chrome.ChromeDriver
import org.slf4j.LoggerFactory

class SeleniumUtil(
        private val dreamGamingSelenium: DreamGamingSelenium
) {

    private val log = LoggerFactory.getLogger(SeleniumUtil::class.java)
    private val cache = hashMapOf<String, ChromeDriver>()

    private fun getHashKey(bind: PlatformBind): String {
        return "${bind.clientId}:$${bind.platform}"
    }

    private fun initChrome(hashId: String, bind: PlatformBind, selenium: PlatformSelenium): ChromeDriver {
        val driver = ChromeDriver()
        cache[hashId] = driver

        val initReq = SeleniumInitReq(username = bind.username, password = bind.password)
        selenium.clickToReportPage(driver = driver, initReq = initReq)

        return driver
    }

    private fun getChromeDriver(bind: PlatformBind, selenium: PlatformSelenium): ChromeDriver {
        val hashId = getHashKey(bind = bind)
        return cache[getHashKey(bind = bind)] ?: initChrome(hashId = hashId, bind = bind, selenium = selenium)
    }

    fun control(bind: PlatformBind, parseReq: SeleniumParseReq): List<SeleniumData> {

        val platformSelenium = when (bind.platform) {
            Platform.DreamGaming -> dreamGamingSelenium
            else -> {
                error("")
            }
        }

        val driver = this.getChromeDriver(bind = bind, selenium = platformSelenium)

        return try {
            platformSelenium.parseHtmlToData(driver = driver, parseReq = parseReq)
        } catch (e: Exception) {
            log.error("解析错误", e)

            val hashId = getHashKey(bind = bind)
            cache[hashId]?.quit()
            cache.remove(hashId)

            throw e
        }
    }

    fun quit(bind: PlatformBind) {
        val hashId = getHashKey(bind = bind)
        cache[hashId]?.quit()
        cache.remove(hashId)
    }


}