package com.onepiece.gpgaming.mr.selenium.impl

import com.onepiece.gpgaming.mr.selenium.PlatformSelenium
import com.onepiece.gpgaming.mr.selenium.SeleniumData
import com.onepiece.gpgaming.mr.selenium.WebDriverWaitUtil
import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.ChromeDriver
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class CMDSelenium : PlatformSelenium {
    override fun clickToReportPage(driver: ChromeDriver, initReq: SeleniumInitReq) {
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR).toString()
        val month = if (date.get(Calendar.MONTH) < 10) "0" + date.get(Calendar.MONTH).toString() else date.get(Calendar.MONTH).toString()
        val day = if (date.get(Calendar.DATE) < 10) "0" + date.get(Calendar.DATE).toString() else date.get(Calendar.DATE).toString()
        val url = "http://bo.flashtechsolution.net/Admin/Report/Bo/LicenseeWinLose/LicenseeWinLoseCurr.aspx?resellerCode=&merChantCode=GP8A&curr=MYR&workdate=${year}${month}${day}&datefrom=${month}-${day}-${year}&dateto=${month}-${day}-${year}&filter=0&checkOld=false&checkusd=true"
        WebDriverWaitUtil(driver)
                .start("http://bo.flashtechsolution.net/")
                .input(msg = "CMD 商户号", id = "partnerCode", content = "gp8a")
                .input(msg = "CMD 用户名", id = "userName", content = "gp8asub")
                .input(msg = "CMD 密码", id = "password", content = "Abcd1234")
                .clickById(msg = "CMD 登录", id = "btn-login")

                .wait(msg = "CMD 等待加载", timeWait = 3000L)
                .start(url = url)
    }

    override fun parseHtmlToData(driver: ChromeDriver, parseReq: SeleniumParseReq): List<SeleniumData> {
        val seleniumDataListUser = WebDriverWaitUtil(driver)
//        return WebDriverWaitUtil(driver)
                .verify(msg = "CMD 验证空数据") {
                    it.findElementsByXPath("//*[@id=\"tbody\"]/tr").size > 2
//                    text != "no data found"
                }
                .nextPage(
                        //CMD 不存在下一页情况
                        verify = {
                            false to it.findElementById("tablelist")
                        },
                        handler = { it ->
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("tablelist")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .filter {
//                                        it.getElementsByTag("td").size > 8
                                        it.text().split(" ").size == 11
                                    }
                                    .map { tr ->
                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[1].text()
                                        val betFrequency = tds[4].text().toInt()
                                        val totalBet = tds[5].text().replace(",","").toBigDecimal()
                                        val totalValidBet = 0.toBigDecimal()
                                        val memberProfit = tds[8].text().replace(",","").toBigDecimal()
                                        val agentProfit = 0.toBigDecimal()
                                        val companyProfit = 0.toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })


        val seleniumDataTotal = WebDriverWaitUtil(driver)
                .nextPage(
                        verify = {
                            false to it.findElementById("tablelist")
                        },
                        handler = { it ->
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("tablelist")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .filter {
                                        it.text().split(" ").size == 9
                                    }
                                    .map { tr ->
                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[0].text()
                                        val betFrequency = tds[3].text().toInt()
                                        val totalBet = tds[4].text().replace(",","").toBigDecimal()
                                        val totalValidBet = 0.toBigDecimal()
                                        val memberProfit = tds[7].text().replace(",", "").toBigDecimal()
                                        val agentProfit = 0.toBigDecimal()
                                        val companyProfit = 0.toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })

        val seleniumDataTotalInUSD = WebDriverWaitUtil(driver)
                .nextPage(
                        verify = {
                            false to it.findElementById("tablelist")
                        },
                        handler = { it ->
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("tablelist")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .filter {
                                        it.text().split(" ").size == 10
                                    }
                                    .map { tr ->
                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[0].text()
                                        val betFrequency = tds[1].text().toInt()
                                        val totalBet = tds[2].text().replace(",","").toBigDecimal()
                                        val totalValidBet = 0.toBigDecimal()
                                        val memberProfit = tds[5].text().replace(",", "").toBigDecimal()
                                        val agentProfit = 0.toBigDecimal()
                                        val companyProfit = 0.toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })

        return seleniumDataListUser + seleniumDataTotal + seleniumDataTotalInUSD

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