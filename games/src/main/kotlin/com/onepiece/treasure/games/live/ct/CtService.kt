//package com.onepiece.treasure.games.live.ct
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.onepiece.treasure.beans.enums.Language
//import com.onepiece.treasure.beans.enums.LaunchMethod
//import com.onepiece.treasure.beans.enums.Platform
//import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
//import com.onepiece.treasure.beans.model.token.DefaultClientToken
//import com.onepiece.treasure.beans.value.database.BetOrderValue
//import com.onepiece.treasure.beans.value.order.BetCacheVo
//import com.onepiece.treasure.core.OnePieceRedisKeyConstant
//import com.onepiece.treasure.core.order.CTBetOrder
//import com.onepiece.treasure.core.order.CTBetOrderDao
//import com.onepiece.treasure.core.service.BetOrderService
//import com.onepiece.treasure.games.GameValue
//import com.onepiece.treasure.games.PlatformApi
//import com.onepiece.treasure.games.http.OkHttpUtil
//import com.onepiece.treasure.utils.RedisService
//import org.apache.commons.codec.digest.DigestUtils
//import org.springframework.stereotype.Service
//import java.math.BigDecimal
//import java.time.LocalDateTime
//import java.util.*
//
///**
// * 币种支持
// * 1	CNY	人民币
//2	USD	美元
//3	MYR	马来西亚币
//4	HKD	港币
//5	THB	泰珠
//6	SGD	新加坡元
//7	PHP	菲律宾比索
//8	TWD	台币
//9	VND	越南盾
//10	IDR	印尼(盾)
//11	JPY	日元
//12	KHR	柬埔寨币
//13	KWR	韩元
//16	AUD	澳大利亚元
//19	INR	印度卢比
//20	EUR	欧元
//21	GBP	英镑
//22	CAD	加拿大
//23	KRW2	韩元	已去除3个0，游戏中1块，等同于实际1000块
//24	MMK	缅甸币
//25	MMK2	缅甸币	已去除3个0，游戏中1块，等同于实际1000块
//29	VND2	越南盾	已去除3个0，游戏中1块，等同于实际1000块
//30	IDR2	印尼(盾)	已去除3个0，游戏中1块，等同于实际1000块
//100	TEST	测试币
// */
//@Service
//class CtService : PlatformApi() {
//
//    // 暂时用马币
//    val currency = "MYR"
////    val lang = "en"
//
//    fun checkCode(codeId: Int) {
//        when (codeId) {
//            0 -> {}
//            300 -> { error(OnePieceExceptionCode.PLATFORM_AEGIS)}
//            else -> { error(OnePieceExceptionCode.PLATFORM_REQUEST_ERROR) }
//        }
//    }
//
//
//    override fun register(registerReq: GameValue.RegisterReq): String {
//        val param = CtBuild.instance(registerReq.token as DefaultClientToken, "signup")
//
//        val md5Password = DigestUtils.md5Hex(registerReq.password)
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "data":"G",
//                "member":{
//                    "username":"${registerReq.username}",
//                    "password":"$md5Password",
//                    "currencyName":"$currency",
//                    "winLimit":1000
//                }
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.SignupResult::class.java)
//        checkCode(result.codeId)
//
//        return registerReq.username
//    }
//
//    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
//        val param = CtBuild.instance(balanceReq.token as DefaultClientToken,"getBalance")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "member":{"username":"${balanceReq.username}"}
//            }
//        """.trimIndent()
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.BalanceResult::class.java)
//        return result.member.balance
//    }
//
//    override fun transfer(transferReq: GameValue.TransferReq): String {
//        val param = CtBuild.instance(transferReq.token as DefaultClientToken, "transfer")
//
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "data":"${transferReq.orderId}",
//                "member":{
//                    "username":"${transferReq.username}",
//                    "amount":${transferReq.amount}
//                }
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.Transfer::class.java)
//        return result.data
//    }
//
//    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
//        val param = CtBuild.instance(checkTransferReq.token as DefaultClientToken, "transfer")
//
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "data":"${checkTransferReq.orderId}"
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.CheckTransferResult::class.java)
//        return result.codeId == 0
//    }
//
//    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
//
//        val param = CtBuild.instance(token = pullBetOrderReq.token as DefaultClientToken, method = "getReport")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}"
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtBetOrder::class.java)
//        checkCode(result.codeId)
//
//        val orders = result.getBetOrders(objectMapper)
//
//        // 过滤已结算的
//        val ids = orders.map { it.orderId }
//        this.mark(token = pullBetOrderReq.token, ids = ids)
//
//        return orders
//    }
//
////    override fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {
////        val processId = UUID.randomUUID().toString().replace("-", "")
////
////        val param = CtBuild.instance(token = syncBetOrderReq.token as DefaultClientToken, method = "getReport")
////        val data = """
////            {
////                "token":"${param.token}",
////                "random":"${param.random}"
////            }
////        """.trimIndent()
////
////        val result = okHttpUtil.doPostJson(param.url, data, CtValue.Report::class.java)
////        checkCode(result.codeId)
////
////        if (result.list == null) return processId
////
////
////        val now = LocalDateTime.now()
////        val orders = result.list.map {
////
////            val username = it.userName
////            val clientId = username.substring(1, 4).toInt()
////            val memberId = username.substring(4, username.length).toInt()
////            with(it) {
////                CTBetOrder(id = id, clientId = clientId, memberId = memberId, lobbyId = lobbyId, platformMemberId = it.memberId, shoeId = shoeId,
////                        tableId = tableId, playId = playId, gameId = gameId, gameType = gameType, betTime = betTime, calTime = calTime, winOrLoss = winOrLoss,
////                        winOrLossz = winOrLossz, betPointsz = betPointsz, betPoints = betPoints, betDetailz = betDetailz, betDetail = betDetail,
////                        balanceBefore = balanceBefore, parentBetId = parentBetId, availableBet = availableBet, ip = ip, ext = ext, isRevocation = isRevocation,
////                        currencyId = currencyId, deviceType = deviceType, pluginId = pluginId, result = it.result, userName = userName, createdTime = now)
////            }
////        }
////        // 存储订单
////        ctBetOrderDao.create(orders)
////
////        // 放到缓存
////        val caches = orders.groupBy { it.memberId }.map {
////            val memberId = it.key
////            val money = it.value.sumByDouble { it.betPoints.toDouble() }.toBigDecimal().setScale(2, 2)
////
////            //TODO 暂时
////            BetCacheVo(memberId = memberId, bet = money, platform = Platform.CT, win = BigDecimal.ZERO)
////        }
////        val redisKey = OnePieceRedisKeyConstant.betCache(processId)
////        redisService.put(redisKey, caches)
////
////        // 过滤已结算的
////        val ids = result.list.filter { it.isRevocation == 1 }.map { it.id }
////        this.mark(token = syncBetOrderReq.token, ids = ids)
////
////        return processId
////    }
//
//    private fun mark(token: DefaultClientToken, ids: List<String>) {
//
//        val list = ids.joinToString(separator = ",")
//        val param = CtBuild.instance(token = token, method = "mark")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "list":[$list]
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.Mark::class.java)
//        checkCode(result.codeId)
//
//    }
//
//    override fun start(startReq: GameValue.StartReq): String {
//
//        val lang = when (startReq.language) {
//            Language.EN -> "en"
//            Language.CN -> "cn"
//            Language.TH -> "th"
//            else -> "en"
//        }
//
//
//        val param = CtBuild.instance(startReq.token as DefaultClientToken, "login")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "lang":"$lang",
//                "member":{
//                    "username":"${startReq.username}"
//                }
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.LoginResult::class.java)
//        checkCode(result.codeId)
//
//        return when (startReq.startPlatform) {
//            LaunchMethod.Web -> result.list[0]
//            LaunchMethod.Wap -> result.list[1]
//            else -> result.list[2]
//        }.plus(result.token)
//    }
//
//    open fun startDemo(token: DefaultClientToken, startPlatform: LaunchMethod, language: Language): String {
//
//        val lang = when (language) {
//            Language.EN -> "en"
//            Language.CN -> "cn"
//            Language.TH -> "th"
//            else -> "en"
//        }
//
//        val param = CtBuild.instance(token, "free")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "lang":"$lang",
//                "device": 1
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, CtValue.LoginResult::class.java)
//        checkCode(result.codeId)
//
//        return when (startPlatform) {
//            LaunchMethod.Web -> result.list[0]
//            LaunchMethod.Wap -> result.list[1]
//            else -> result.list[2]
//        }.plus(result.token)
//    }
//
//
//}