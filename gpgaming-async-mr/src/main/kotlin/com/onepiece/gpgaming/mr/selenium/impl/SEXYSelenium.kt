package com.onepiece.gpgaming.mr.selenium.impl

import com.onepiece.gpgaming.mr.selenium.PlatformSelenium
import com.onepiece.gpgaming.mr.selenium.SeleniumData
import com.onepiece.gpgaming.mr.selenium.WebDriverWaitUtil
import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.ChromeDriver

class SEXYSelenium : PlatformSelenium {
    override fun clickToReportPage(driver: ChromeDriver, initReq: SeleniumInitReq) {
        WebDriverWaitUtil(driver)
                .start("https://ag.onlinegames22.com/page/login/agentLogin.jsp")
                //登录环节
                .input(msg = "SEXY 输入用户名:", id = "account", content = "subu996api1")
                .input(msg = "SEXY 输入密码:", id = "password", content = "Abcd@1234")
                .clickByXPath(msg = "SEXY 登录", xpath = "//*[@id=\"login_wrap\"]/div/ul/li[3]/button")
                //页面跳转
                .clickById(msg = "SEXY 展开Report菜单", id = "payReport")
                .clickById(msg = "SEXY 进入输赢报表界面", id = "detailType")

    }

    override fun parseHtmlToData(driver: ChromeDriver, parseReq: SeleniumParseReq): List<SeleniumData> {
        return WebDriverWaitUtil(driver)
                .next(msg = "SEXY 切换句柄") {
                    val handles = it.windowHandles.iterator()
                    val currentHandle = it.windowHandle
                    while (handles.hasNext()) {
                        val newHandles = handles.next()
                        if (currentHandle == newHandles) {
                            continue
                        }
                        try {
//                            val newHandle = handles.next()
                            it.switchTo().window(newHandles)
                        } catch (e: Exception) {
                        }
                    }
                }
                .clickById(msg = "SEXY 选择查询时间Today", id = "yesterdayLink")

                .verify(msg = "SEXY 验证空数据") {
                    val text = it.findElementByXPath("//*[@id=\"contentDetailBodyEmpty\"]/tr/td").text
                    text != "no data found"
                }
                .nextPage(
                        //SEXY 不存在下一页情况
                        verify = {
                            false to it.findElementById("submitDateChange")
                        },
                        handler = {
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("detailedTable")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .map { tr ->
                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[1].text()
                                        val betFrequency = tds[4].text().toInt()
                                        val totalBet = (if (tds[5].text().contains("(")) toBigDWithBrackets(tds[5].text()) else toBigDNoBrackets(tds[5].text()))!!.toBigDecimal()
                                        val totalValidBet = 0.toBigDecimal()
                                        val memberProfit = (if (tds[21].text().contains("(")) toBigDWithBrackets(tds[21].text()) else toBigDNoBrackets(tds[21].text()))!!.toBigDecimal()
                                        val agentProfit = (if (tds[24].text().contains("(")) toBigDWithBrackets(tds[24].text()) else toBigDNoBrackets(tds[24].text()))!!.toBigDecimal()
                                        val companyProfit = (if (tds[41].text().contains("(")) toBigDWithBrackets(tds[41].text()) else toBigDNoBrackets(tds[41].text()))!!.toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })
    }

    private fun toBigDWithBrackets(text: String): String? {
        return try {
            text.split("(")[1].split(")")[0].replace(",", "").replace("$", "")
        } catch (e: Exception) {
            null
        }
    }

    private fun toBigDNoBrackets(text: String): String? {
        return try {
            text.replace(",", "").replace("$", "")
        } catch (e: Exception) {
            null
        }
    }
}