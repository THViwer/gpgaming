package com.onepiece.gpgaming.mr.selenium.impl

import com.onepiece.gpgaming.mr.selenium.PlatformSelenium
import com.onepiece.gpgaming.mr.selenium.SeleniumData
import com.onepiece.gpgaming.mr.selenium.WebDriverWaitUtil
import com.onepiece.gpgaming.mr.selenium.param.SeleniumInitReq
import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class DreamGamingSelenium : PlatformSelenium {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

    override fun clickToReportPage(driver: ChromeDriver, initReq: SeleniumInitReq) {
        WebDriverWaitUtil(driver)
                .start("https://ag.dg66.info/ag/login.html")
                // 输入用户名和密码进行登陆
                .inputByXPath(msg = "输入用户名", xpath = "/html/body/div[2]/div/div[3]/form/input[1]", content = initReq.username)
                .inputByXPath(msg = "输入密码", xpath = "/html/body/div[2]/div/div[3]/form/input[2]", content = initReq.password)
                .clickByXPath(msg = "开始登陆", xpath = "/html/body/div[2]/div/div[3]/form/div")

                // 点击menu进入报表页面
                .clickByXPath(msg = "报表管理", xpath = "/html/body/div[1]/aside/section/ul/li[3]/a/span")
                .clickByXPath(msg = "输赢报表", xpath = "/html/body/div[1]/aside/section/ul/li[3]/ul/li[1]/a")
    }


    override fun parseHtmlToData(driver: ChromeDriver, parseReq: SeleniumParseReq): List<SeleniumData> {
        return WebDriverWaitUtil(driver)

                // 输入时间 点击查询
                .inputByXPath(msg = "开始时间", xpath = "/html/body/div[1]/div/section[2]/div/div/form/div[1]/input[1]", content = parseReq.startDateTime.format(dateFormat))
                .inputByXPath(msg = "结束时间", xpath = "/html/body/div[1]/div/section[2]/div/div/form/div[1]/input[2]", content = parseReq.endDateTime.format(dateFormat))
                .clickByXPath(msg = "点击查询", xpath = "/html/body/div[1]/div/section[2]/div/div/form/button")

                .verify(msg = "验证空数据") {
                    val total = it.findElementByXPath("/html/body/div[1]/div/section[2]/div/div/form/div[5]/div/div[1]/div/div[2]/table/tfoot/tr/td[2]").text
                    total != "0"
                }
                // 进入用户报表页面
                .clickByXPath(msg = "用户报表", xpath = "/html/body/div[1]/div/section[2]/div/div/form/div[5]/div/div[1]/div/div[2]/table/tbody/tr[1]/td[2]/a")
//                .next(msg = "选择分页") {
//                    val selectElement = it.findElementByXPath("/html/body/div[1]/div/section[2]/div/div/form/div[6]/div/div/select")
//                    Select(selectElement).selectByIndex(0)
//                }
                .nextPage(
                        verify = {
                            val lis = it.findElementByXPath("/html/body/div[1]/div/section[2]/div/div/form/div[7]/div/div/ul")
                                    .findElements(By.tagName("li"))
                            val li = lis.first { li -> li.getAttribute("class").contains("next") }

                            val nextPageBtn = li.findElement(By.tagName("a"))
                            !li.getAttribute("class").contains("disabled") to nextPageBtn
                        },
                        handler = {
                            val source = it.pageSource
                            val jsoup = Jsoup.parse(source)
                            val table = jsoup.getElementById("winorlossdetailTable")

                            table.getElementsByTag("tbody")
                                    .first()
                                    .getElementsByTag("tr")
                                    .map { tr ->
                                        val tds = tr.getElementsByTag("td")

                                        val username = tds[1].text()
                                        val betFrequency = tds[4].text().toInt()
                                        val totalBet = tds[5].text().replace(",", "").toBigDecimal()
                                        val totalValidBet = tds[6].text().replace(",", "").toBigDecimal()
                                        val memberProfit = tds[7].text().replace(",", "").toBigDecimal()
                                        val agentProfit = tds[10].text().replace(",", "").toBigDecimal()
                                        val companyProfit = tds[13].text().replace(",", "").toBigDecimal()

                                        SeleniumData(username = username, betFrequency = betFrequency, totalBet = totalBet, totalValidBet = totalValidBet,
                                                memberProfit = memberProfit, agentProfit = agentProfit, companyProfit = companyProfit)
                                    }
                        })
    }
}