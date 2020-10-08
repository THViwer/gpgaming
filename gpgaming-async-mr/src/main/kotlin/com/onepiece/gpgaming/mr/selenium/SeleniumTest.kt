//package com.onepiece.gpgaming.mr.selenium
//
//import com.onepiece.gpgaming.beans.enums.Platform
//import com.onepiece.gpgaming.beans.enums.Status
//import com.onepiece.gpgaming.beans.model.PlatformBind
//import com.onepiece.gpgaming.mr.selenium.impl.DreamGamingSelenium
//import com.onepiece.gpgaming.mr.selenium.param.SeleniumParseReq
//import java.math.BigDecimal
//import java.time.LocalDate
//import java.time.LocalDateTime
//
//fun main() {
//    System.setProperty("webdriver.chrome.driver", "/Users/cabbage/workspace/chromedriver")
//
//    val dreamGamingSelenium = DreamGamingSelenium()
//    val seleniumUtil = SeleniumUtil(dreamGamingSelenium = dreamGamingSelenium)
//
//    val bind = PlatformBind(id = 0, clientId = 1, platform = Platform.DreamGaming, hot = false, new = false, username = "DG03210103sub01", password = "Abcd1234",
//            earnestBalance = BigDecimal.ZERO, tokenJson = "", name = "", icon = "", disableIcon = null, originIconOver = "", originIcon = "",
//            mobileIcon = "", mobileDisableIcon = null, platformDetailIconOver = null, platformDetailIcon = null, processId = "", status = Status.Normal,
//            createdTime = LocalDateTime.now())
//
//    val startDateTime = LocalDate.now().atStartOfDay().minusDays(1000)
//    val endDateTime = LocalDate.now().plusDays(1).atStartOfDay().minusSeconds(1)
//    val parseReq = SeleniumParseReq(startDateTime = startDateTime, endDateTime = endDateTime)
//    val list = seleniumUtil.control(bind = bind, parseReq = parseReq)
//
//
//    println(list)
//    seleniumUtil.quit(bind = bind)
//    println("--------end--------")
//    println("--------end--------")
//    println("--------end--------")
//    println("--------end--------")
//}
//
//
////class SeleniumTest {
////
////    private val log = LoggerFactory.getLogger(SeleniumTest::class.java)
////    private val path = "https://ag.dg66.info/ag/login.html"
////
////    var chrome: ChromeDriver = ChromeDriver()
////    var _driverUtil: WebDriverWaitUtil? = null
////
////    fun init(): WebDriverWaitUtil {
////        chrome.get(path)
////        val driverUtil = WebDriverWaitUtil(chrome = chrome)
////        _driverUtil = driverUtil
////        return driverUtil
////    }
////
////
////    // 登陆并进入到报表页面
////    fun login(): WebDriverWaitUtil {
////        val driverUtil = _driverUtil ?: init()
////
////        return driverUtil
////                // 输入用户名和密码进行登陆
////                .inputByXPath(msg = "", xpath = "/html/body/div[2]/div/div[3]/form/input[1]", content = "DG03210103sub01")
////                .inputByXPath(msg = "", xpath = "/html/body/div[2]/div/div[3]/form/input[2]", content = "Abcd1234")
////                .clickByXPath(msg = "", xpath = "/html/body/div[2]/div/div[3]/form/div")
////
////                // 点击menu进入报表页面
////                .clickByXPath(msg = "", xpath = "/html/body/div[1]/aside/section/ul/li[3]/a/span")
////                .clickByXPath(msg = "", xpath = "/html/body/div[1]/aside/section/ul/li[3]/ul/li[1]/a")
////
////                // 输入时间 点击查询
////                .inputByXPath(msg = "", xpath = "/html/body/div[1]/div/section[2]/div/div/form/div[1]/input[1]", content = "2020/10/08 00:00:00")
////                .inputByXPath(msg = "", xpath = "/html/body/div[1]/div/section[2]/div/div/form/div[1]/input[2]", content = "2020/10/08 23:59:59")
////                .clickByXPath(msg = "", xpath = "/html/body/div[1]/div/section[2]/div/div/form/button")
////    }
////
////    fun parseHtmlToData() {
////        val driverUtil = _driverUtil ?: login()
////
////        driverUtil.next(msg = "") {
////            val source = it.pageSource
////            val jsoup = Jsoup.parse(source)
////            val table = jsoup.getElementById("winorlossdetailTable")
////
////            table.getElementsByTag("tbody")
////                    .first()
////                    .getElementsByTag("tr")
////                    .map { tr ->
////                        val tds = tr.getElementsByTag("td")
////
////                    }
////
////
////            val tableHtml = table.html()
////            log.info(tableHtml)
////            it
////        }
////
////    }
////
////
////}
////
////fun main() {
////
////    System.setProperty("webdriver.chrome.driver", "/Users/cabbage/workspace/chromedriver")
////
////
////    val test = SeleniumTest()
////    test.login()
////    test.parseHtmlToData()
////
////    Thread.sleep(30 * 1000)
////}