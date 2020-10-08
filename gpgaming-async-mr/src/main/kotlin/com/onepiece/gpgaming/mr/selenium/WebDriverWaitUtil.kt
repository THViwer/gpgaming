package com.onepiece.gpgaming.mr.selenium

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class WebDriverWaitUtil(
        val chrome: RemoteWebDriver
) {

    val log = LoggerFactory.getLogger(WebDriverWaitUtil::class.java)

    private var successful = true
    private var errorCode = -1
    private val _timeoutSecond = 30L // 测试的时候超时为60s
    private val _minAmount = 0L //最小充值金额
    private val _maxAmountMBB = BigDecimal.valueOf(6000)
    private val _maxAmountCIMB = BigDecimal.valueOf(10000)
    private var flagSecurityChallengeQuestion = false


    private fun printLog(msg: String, e: Exception? = null) {
        log.info("开始操作：$msg")
        if (e != null) {
            log.info("错误码=$errorCode", e)
        }
    }

    fun printPage(): WebDriverWaitUtil {
        log.info(chrome.pageSource)
        log.info(chrome.title)
        log.info(chrome.currentUrl)
        return this
    }

    fun start(url: String): WebDriverWaitUtil {
        this.printLog(msg = "开始进行转账操作")
        chrome.get(url)
        return this
    }

    fun getCurrentURL(): WebDriverWaitUtil {
        this.printLog(msg = "当前URL：${chrome.currentUrl}")
        return this
    }

    fun verify(msg: String, timeoutSecond: Long = _timeoutSecond, executeState: Boolean = false, function: (chrome: RemoteWebDriver) -> Boolean): WebDriverWaitUtil {
        if (!successful) return this

        this.printLog(msg = msg)

        try {
            WebDriverWait(chrome, timeoutSecond).until {
                try {
                    successful = function(it as RemoteWebDriver)
                    log.info("---------successful = $successful")

                    true
                } catch (e: Exception) {
                    this.printLog(msg = "$msg loading......")
//                    this.getCurrentURL()
                    Thread.sleep(100)
                    false
                }
            }
        } catch (e: Exception) {
//            this.printLog(msg = chrome.pageSource)
            this.errorCode = PayErrorCode.POSITION_ERROR
            this.printLog(msg = "转账失败", e = e)
            successful = executeState
        }

        return this
    }

    // 默认超时为10s
    fun next(msg: String, timeoutSecond: Long = _timeoutSecond, executeState: Boolean = false, function: (chrome: RemoteWebDriver) -> Any): WebDriverWaitUtil {
        if (!successful) return this

        this.printLog(msg = msg)

        try {
            WebDriverWait(chrome, timeoutSecond).until {
                try {
                    val any = function(it as RemoteWebDriver)

                    if (any is Boolean) {
                        any
                    } else {
                        true
                    }
                } catch (e: Exception) {
                    this.printLog(msg = "$msg loading......")
//                    this.getCurrentURL()
                    Thread.sleep(100)
                    false
                }
            }
        } catch (e: Exception) {
//            this.printLog(msg = chrome.pageSource)
            this.errorCode = PayErrorCode.POSITION_ERROR
            this.printLog(msg = "转账失败", e = e)
            successful = executeState
        }

        return this
    }

    fun <T> nextPage(verify: (driver: RemoteWebDriver) -> Pair<Boolean, WebElement>, handler:(driver: RemoteWebDriver) -> List<T>): List<T> {
        if (!successful) return emptyList()

        val total = arrayListOf<T>()

        fun verifyAndNextPage(): Boolean {
            var hasNext = false
            var nextPageBtn: WebElement ? = null


            this.next(msg = "verify") {
                val (_hasNext, _nextPageBtn) = verify(it)
                hasNext = _hasNext
                nextPageBtn = _nextPageBtn

                true
            }

            if (hasNext) {
                this.next(msg = "") {
                    nextPageBtn?.click()
                    0
                }
            }

            return hasNext
        }
        do {

            this.next(msg = "next page") {
                val list = handler(it)
                total.addAll(list)

                this
            }

        } while (verifyAndNextPage())

        return total
    }

    fun <T> complete(function: (driver: RemoteWebDriver, source: String) -> T): T {
        var source: String = ""
        val webDriverWaitUtil = this.next(msg = "complete") {
            source = it.pageSource
            this
        }
        return function(webDriverWaitUtil.chrome, source)
    }

    fun closeATByIdAndClass(msg: String, id: String, className: String, index: Int): WebDriverWaitUtil {
        try {
            WebDriverWait(chrome, 5).until {
                try {
                    chrome.findElementByXPath("//*[@id=\"${id}\"]/div").isDisplayed
                } catch (e: Exception) {
                    Thread.sleep(400L)
                }
            }
            val elem = "var elem = document.getElementById(\"$id\");elem.parentNode.removeChild(elem);"
            val js = chrome as JavascriptExecutor
            js.executeScript(elem)

            val elem1 = "var elem = document.getElementsByClassName(\"$className\")[$index];elem.parentNode.removeChild(elem);"
            js.executeScript(elem1)
        } catch (e: Exception) {
            printLog(msg = "$msg, --无广告")
        }
        return this
    }

    fun closeATByIdAndId(msg: String, id1: String, id2: String): WebDriverWaitUtil {
        try {
            val elem = "var elem = document.getElementById(\"$id1\");elem.parentNode.removeChild(elem);"
            val js = chrome as JavascriptExecutor
            js.executeScript(elem)

            val elem1 = "var elem = document.getElementById(\"$id2\");elem.parentNode.removeChild(elem);"
            js.executeScript(elem1)
        } catch (e: Exception) {
            printLog(msg = "$msg, ---无广告")
        }
        return this
    }

    fun closeATById(msg: String, id1: String, id2: String): WebDriverWaitUtil {
        try {
            WebDriverWait(chrome, 5).until {
                try {
                    chrome.findElementByXPath("//*[@id=\"${id1}\"]/div").isDisplayed
                } catch (e: Exception) {
                    Thread.sleep(400L)
                }
            }
            val elem = "var elem = document.getElementById(\"$id1\");elem.parentNode.removeChild(elem);"
            val js = chrome as JavascriptExecutor
            js.executeScript(elem)

            val elem1 = "var elem = document.getElementById(\"$id2\");elem.parentNode.removeChild(elem);"
            js.executeScript(elem1)
        } catch (e: Exception) {
            printLog(msg = "$msg, --无广告")
        }
        return this
    }

    fun closeATJS(msg: String, className1: String, className2: String, index1: Int, index2: Int): WebDriverWaitUtil {
        try {
            Thread.sleep(1000L)
            WebDriverWait(chrome, _timeoutSecond).until {
                try {
                    chrome.findElementByClassName(className1).isDisplayed
                } catch (e: Exception) {
                    Thread.sleep(200L)
                }
            }
            val elem1 = "var elem = document.getElementsByClassName(\"$className1\")[$index1];elem.parentNode.removeChild(elem);"
            val js = chrome as JavascriptExecutor
            js.executeScript(elem1)

            val elem2 = "var elem = document.getElementsByClassName(\"$className2\")[$index2];elem.parentNode.removeChild(elem);"
            js.executeScript(elem2)
            printLog(msg = "$msg, 广告已关闭")
        } catch (e: Exception) {
            printLog(msg = "$msg, 关闭广告失败")
        }

        return this
    }

    fun switchToParentFrame(msg: String): WebDriverWaitUtil {
        try {
            printLog(msg = msg)
            chrome.switchTo().parentFrame()
        } catch (e: Exception) {
            printLog(msg = msg, e = e)
        }
        return this
    }

    fun switchToFrame(msg: String, frame: String): WebDriverWaitUtil {
//        this.next(msg = msg) {
//            ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame).apply(it)!!
//        }
        printLog(msg = msg)
        try {
            ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame).apply(chrome)
        } catch (e: Exception) {
            printLog(msg = "切换frame失败", e = e)
        }
        return this
    }

    fun clickByXPath(msg: String, xpath: String, index: Int? = null): WebDriverWaitUtil {

        when (index) {
            null -> {
                this.next(msg = msg) {
                    it.findElementByXPath(xpath).click()
                }
            }
            else -> {
                this.next(msg = msg) {
                    it.findElementsByXPath(xpath)[index].click()
                }
            }
        }

        return this
    }

    fun clickByCss(msg: String, css: String, index: Int? = null): WebDriverWaitUtil {

        when (index) {
            null -> {
                this.next(msg = msg) {
                    it.findElementByCssSelector(css).click()
                }
            }
            else -> {
                this.next(msg = msg) {
                    it.findElementsByCssSelector(css)[index].click()
                }
            }
        }

        return this
    }


    fun clickVerifyByXpath(msg: String, xpathSelected: String, xpath: String, index: Int? = null): WebDriverWaitUtil {
        when (index) {
            null -> {
                this.next(msg = msg) {
                    if (!verifySelectByXpathBoolean(xpathSelected = xpathSelected)) {
                        it.findElementByXPath(xpath).click()
                    }
                }
            }
            else -> {
                this.next(msg = msg) {
                    if (!verifySelectByXpathBoolean(xpathSelected = xpathSelected, index = index)) {
                        it.findElementsByXPath(xpath)[index].click()
                    }
                }
            }
        }

        return this
    }

    fun clickById(msg: String, id: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementById(id).click()
        }
        return this
    }

    fun wait(msg: String, timeWait: Long): WebDriverWaitUtil {
        printLog(msg = msg)
        Thread.sleep(timeWait)
        return this
    }

    fun clickByName(msg: String, name: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementByName(name).click()
        }
        return this
    }

    fun clickForCondition(msg: String, iframe: Int, xpathCondition: String, conditionString: String, xpath: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            try {
                it.switchTo().frame(iframe)
            } catch (e: Exception) {
            }

            if (ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpathCondition), conditionString).apply(it)!!) {
                it.switchTo().parentFrame()
                it.findElement(By.xpath(xpath)).click()
            }
        }
//        WebDriverWait(chrome, 10).until {
//            try {
//                chrome.switchTo().frame(iframe)
//            } catch (e: Exception) {
//            }
//            try {
//                if (ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpathCondition), conditionString).apply(it)!!) {
//                    it.switchTo().parentFrame()
//                    it.findElement(By.xpath(xpath)).click()
//                    printLog(msg = msg)
//                }
//            } catch (e: Exception) {
//                printLog(msg = "PBB 关闭获取TAC的弹窗-没有展示弹窗。。。")
//            }

//        }
        return this
    }

    fun clickByClassName(msg: String, className: String, index: Int? = null): WebDriverWaitUtil {

        when (index) {
            null -> {
                this.next(msg = msg) {
                    it.findElementByClassName(className).click()
                }
            }
            else -> {
                this.next(msg = msg) {
                    it.findElementsByClassName(className)[index].click()
                }
            }
        }

        return this
    }

    fun input(msg: String, id: String, content: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementById(id).sendKeys(content)
        }
        return this
    }

    fun inputByXPath(msg: String, xpath: String, content: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementByXPath(xpath).click()
            it.findElementByXPath(xpath).clear()
            it.findElementByXPath(xpath).sendKeys(content)
        }
        return this
    }

    fun switchFrame(msg: String, frame: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.switchTo().frame(frame)
        }
        return this
    }

    fun inputById(msg: String, id: String, content: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementById(id).click()
            it.findElementById(id).sendKeys(content)
        }
        return this
    }

    fun inputByName(msg: String, name: String, content: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementByName(name).click()
            it.findElementByName(name).sendKeys(content)
        }
        return this
    }

    fun inputByCss(msg: String, cssSelector: String, content: String): WebDriverWaitUtil {
        this.next(msg = msg) {
            it.findElementByCssSelector(cssSelector).click()
            it.findElementByCssSelector(cssSelector).sendKeys(content)
        }
        return this
    }


    fun addRule(msg: String, errorMsg: Int? = null, function: (chrome: RemoteWebDriver) -> Boolean): WebDriverWaitUtil {

        when (this.errorCode) {
            PayErrorCode.SUCCESSFUL -> {
                this.next(msg = msg) { chrome ->
                    successful = function(chrome)
                    if (!successful) {
                        this.printLog(msg = msg)
                        this.errorCode = errorMsg!!
                    }
                }
            }
            else -> {
                printLog(msg = "addRule Failed, errorCode = $this.errorCode")
            }
        }
        return this
    }

    private fun verifySelectByXpathBoolean(xpathSelected: String, index: Int? = null): Boolean {
        var flag = false
        when (index) {
            null -> {
                try {
                    WebDriverWait(chrome, 3).until {
                        if (chrome.findElementByXPath(xpathSelected).isDisplayed)
                            flag = true
                    }
                } catch (e: TimeoutException) {
                    flag = false
                }
            }
            else -> {
                try {
                    WebDriverWait(chrome, 3).until {
                        if (chrome.findElementsByXPath(xpathSelected)[index].isDisplayed)
                            flag = true
                    }
                } catch (e: TimeoutException) {
                    flag = false
                }
            }
        }
        return flag
    }

}