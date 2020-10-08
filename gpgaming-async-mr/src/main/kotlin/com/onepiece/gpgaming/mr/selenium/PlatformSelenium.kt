package com.onepiece.gpgaming.mr.selenium

import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.openqa.selenium.chrome.ChromeDriver

interface PlatformSelenium {

    // 登陆并进入到报表页面
    fun clickToReportPage(driver: ChromeDriver, initReq: SeleniumInitReq)

    // 解析html数据
    fun parseHtmlToData(driver: ChromeDriver, parseReq: SeleniumParseReq): List<SeleniumData>

}