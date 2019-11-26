//package com.onepiece.treasure.games.sport.sbo
//
//import com.onepiece.treasure.beans.enums.Language
//import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
//import com.onepiece.treasure.beans.model.token.DefaultClientToken
//import com.onepiece.treasure.beans.value.database.BetOrderValue
//import com.onepiece.treasure.games.GameConstant
//import com.onepiece.treasure.games.GameValue
//import com.onepiece.treasure.games.PlatformService
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import java.math.BigDecimal
//import java.time.LocalDate
//
//@Service
//class SboService : PlatformService() {
//
//    private val language = "en"
//    private val log = LoggerFactory.getLogger(SboService::class.java)
//
//    private fun checkCode(code: Int) = check(code == 0) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }
//
//
//    override fun register(registerReq: GameValue.RegisterReq): String {
//        val platformUsername = "${(registerReq.token as DefaultClientToken).appId}${registerReq.username}"
//
//        val param = "registerplayer/${registerReq.token.appId}/player/${platformUsername}/agent/${registerReq.token.appId}/lang/${language}"
//        val url = SboBuild.instance().build(token = registerReq.token, param = param)
//
//        val result = okHttpUtil.doGet(url, SboValue.SboDefaultResult::class.java)
//        this.checkCode(result.error.id)
//
//        return platformUsername
//    }
//
//    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
//        val param = "getplayerbalance/${(balanceReq.token as DefaultClientToken).appId}/player/${balanceReq.username}"
//        val url = SboBuild.instance().build(token = balanceReq.token, param = param)
//
//        val result = okHttpUtil.doGet(url, SboValue.SboPlayerBalanceResult::class.java)
//        this.checkCode(result.error.id)
//
//        return result.balance
//    }
//
//    override fun transfer(transferReq: GameValue.TransferReq): String {
//        val token = transferReq.token as DefaultClientToken
//        return when {
//            transferReq.amount.toDouble() > 0 -> {
//                val param = "deposit/${token.appId}/player/${transferReq.username}/amount/${transferReq.amount}/txnid/${transferReq.orderId}"
//                val url = SboBuild.instance().build(token = token, param = param)
//
//                val result = okHttpUtil.doGet(url, SboValue.SboDepositResult::class.java)
//                this.checkCode(result.error.id)
//
//                result.refno
//            }
//            else -> {
//                // 检查取款金额是否足够
//                val balanceReq = GameValue.BalanceReq(token = transferReq.token, username = transferReq.username, password = "")
//                val balance = this.balance(balanceReq)
//                check(balance.toDouble() >= transferReq.amount.negate().toDouble()) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }
//
//                // 如果amount = full 则是取全部
//                val param = "withdrawal/${token.appId}/player/${transferReq.username}/amount/${transferReq.amount.negate()}/txnid/${transferReq.orderId}"
//                val url = SboBuild.instance().build(token = token, param = param)
//                val result = okHttpUtil.doGet(url, SboValue.SboWithdrawResult::class.java)
//                this.checkCode(result.error.id)
//
//                result.refno
//            }
//        }
//    }
//
//    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
//        val token = checkTransferReq.token as DefaultClientToken
//
//        val param = "checktransactionstatus/${token.appId}/txnid/${checkTransferReq.orderId}"
//        val url = SboBuild.instance().build(token = token, param = param)
//
//        val result = okHttpUtil.doGet(url, SboValue.SboCheckTransferStatusResult::class.java)
//
//        return result.error.id == 0
//    }
//
//    override fun start(startReq: GameValue.StartReq): String {
//        val token = startReq.token as DefaultClientToken
//
//        val param = "login/${token.appId}/player/${startReq.username}"
//        val url = SboBuild.instance().build(token = token, param = param)
//
//        val result = okHttpUtil.doGet(url, SboValue.SboLoginResult::class.java)
//        this.checkCode(result.error.id)
//
//        val lang = when(startReq.language) {
//            Language.EN -> "en"
//            Language.CN -> "zh-cn"
//            Language.TH -> "th-th"
//            Language.ID -> "id-id"
//            else -> "en"
//        }
//
//        // oddstyle = HK, MY, EU
//        // theme=”: black, blue, ocean, green, emerald
//        return "${GameConstant.SBO_START_URL}?token=${result.token}&lang=$lang&oddstyle=MY&theme=ocean"
//    }
//
//
//    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
//
//        val token = pullBetOrderReq.token as DefaultClientToken
//
//        //TODO 不知道agentName是什么
//        val agentName = "xx"
//
//        val param = "sportsfundservice/pullcustomerbetlist/${token.appId}/user${agentName}?startDate=${pullBetOrderReq.startTime}&endDate=${pullBetOrderReq.endTime}"
//        val url = SboBuild.instance().buildAppend(token = token, param = param)
//
//        //TODO 还不知道返回什么样的
//        val result = okHttpUtil.doGet(url, String::class.java)
//
//        log.error("sbo 查询订单返回结果：$result")
//
//        error(result)
//
//    }
//
//
//
//
//
//    open fun getCustomerReport(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): SboValue.PlayerRevenue {
//
//        // type: agent or player
//        val param = "getcustomerreport/${token.appId}/user/${username}/type/player"
//        val url = SboBuild.instance().build(token = token, param = param)
//
//        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerReportResult::class.java)
//        this.checkCode(result.error.id)
//
//        return result.playerRevenue.first()
//    }
//
//    open fun getCustomerBetList(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<SboValue.PlayerBet> {
//
//        val param = "getcustomerbetlist/${token.appId}/user/${username}"
//        val url = SboBuild.instance().build(token = token, param = param)
//
//
//        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerBetResult::class.java)
//        this.checkCode(result.error.id)
//
//        return result.playerBetList
//
//    }
//}