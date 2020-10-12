package com.onepiece.gpgaming.mr.selenium.impl

import com.onepiece.gpgaming.mr.selenium.PlatformSelenium
import com.onepiece.gpgaming.mr.selenium.SeleniumData
import com.onepiece.gpgaming.mr.selenium.WebDriverWaitUtil
import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.ChromeDriver

class LBCSelenium : PlatformSelenium {
    override fun clickToReportPage(driver: ChromeDriver, initReq: SeleniumInitReq) {
        WebDriverWaitUtil(driver)
                .start("http://portal2.owadmin999.com/etrhvv8/Auth/Login")
                .inputByName(msg = "", name = "UserName", content = "u996sub")
                .inputByName(msg = "", name = "Password", content = "Abcd1234")
                .clickById(msg = "", id = "btn_login")

                //切换一级菜单【报告】
                .clickByXPath(msg = "LBC 打开一级菜单[报告]", xpath = "//*[@id=\"mCSB_1_container\"]/li/ul/li[3]/a/span")
                //进入【会员输赢】
                .clickByXPath(msg = "LBC 进入[会员输赢]", xpath = "//*[@id=\"mCSB_1_container\"]/li/ul/li[3]/div/ul/li[3]/a")


    }

    override fun parseHtmlToData(driver: ChromeDriver, parseReq: SeleniumParseReq): List<SeleniumData> {
        return WebDriverWaitUtil(driver)
                .switchFrame(msg = "LBC 切换iframe", frame = "FunctionFrame")
                .clickById(msg = "LBC 打开日期窗口",id = "DateRange")
                .clickByXPath(msg = "LBC 日期选择[今日]", xpath = "/html/body/div[4]/div[1]/ul/li[1]")
                .next(msg = "LBC 确认币别为MYR") {
                    val myr = it.findElementByXPath("//*[@id=\"select-list-BaseCurrency\"]/div[1]").text
                    if (myr != "MYR") {
                        it.findElementById("select-list-BaseCurrency").click()

                        it.findElementByXPath("//*[@id=\"select-list-BaseCurrency-result-container\"]/div/div/ul/li[contains(text()='MYR')]").click()
                    }
                }
                .clickById(msg = "LBC 查询", id = "SubmitButton")
                .verify(msg = "SEXY 验证空数据") {
                    val text = it.findElementByXPath("//*[@id=\"gview_ReportGrid\"]/div[4]/div/table/tbody/tr[2]/td[4]").text
                    text != "0.00"
                }
                .nextPage(
                        //LBC 不存在翻页情况
                        verify = {
                            false to it.findElementById("SubmitButton")
                        },
                        handler = {
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("ReportGrid")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .next()
                                    .map { tr ->

                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[1].text()
                                        val betFrequency = tds[4].text().toInt()
                                        val totalBet = tds[3].text().replace(",","").toBigDecimal()
                                        val totalValidBet = tds[8].text().replace(",","").toBigDecimal()
                                        val memberProfit = tds[5].text().replace(",", "").toBigDecimal()
                                        val agentProfit = 0.toBigDecimal()
                                        val companyProfit = tds[9].text().replace(",", "").toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })
    }
}