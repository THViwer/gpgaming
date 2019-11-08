package com.onepiece.treasure.games.sport.sbo

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class SboApiService(
        private val okHttpUtil: OkHttpUtil
) : SboApi {

    private val language = "en"

    private fun checkCode(code: Int) = check(code == 0) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

    override fun registerPlayer(clientToken: DefaultClientToken, username: String): String {

        val platformUsername = "${clientToken.appId}${username}"

        val param = "registerplayer/${clientToken.appId}/player/${platformUsername}/agent/${clientToken.appId}/lang/${language}"
        val url = SboBuild.instance().build(token = clientToken, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboDefaultResult::class.java)
        this.checkCode(result.error.id)

        return platformUsername
    }

    override fun login(clientToken: DefaultClientToken, username: String): String {
        val param = "login/${clientToken.appId}/player/${username}"
        val url = SboBuild.instance().build(token = clientToken, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboLoginResult::class.java)
        this.checkCode(result.error.id)

        return "${GameConstant.SBO_START_URL}?token=${result.token}"
    }

    override fun depositOrWithdraw(clientToken: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String {
        return when {
            amount.toDouble() > 0 -> {
                val param = "deposit/${clientToken.appId}/player/${username}/amount/${amount}/txnid/${orderId}"
                val url = SboBuild.instance().build(token = clientToken, param = param)

                val result = okHttpUtil.doGet(url, SboValue.SboDepositResult::class.java)
                this.checkCode(result.error.id)

                result.refno
            }
            else -> {
                // 检查取款金额是否足够
                val balance = this.getPlayerBalance(clientToken = clientToken, username = username)
                check(balance.toDouble() >= amount.negate().toDouble()) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

                // 如果amount = full 则是取全部
                val param = "withdrawal/${clientToken.appId}/player/${username}/amount/${amount}/txnid/${orderId}"
                val url = SboBuild.instance().build(token = clientToken, param = param)
                val result = okHttpUtil.doGet(url, SboValue.SboWithdrawResult::class.java)
                this.checkCode(result.error.id)

                result.refno
            }
        }
    }

    override fun checkTransferStatus(clientToken: DefaultClientToken, orderId: String): Boolean {
        val param = "checktransactionstatus/${clientToken.appId}/txnid/${orderId}"
        val url = SboBuild.instance().build(token = clientToken, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboCheckTransferStatusResult::class.java)

        return result.error.id == 0
    }

    override fun getPlayerBalance(clientToken: DefaultClientToken, username: String): BigDecimal {

        val param = "getplayerbalance/${clientToken.appId}/player/${username}"
        val url = SboBuild.instance().build(token = clientToken, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboPlayerBalanceResult::class.java)
        this.checkCode(result.error.id)

        return result.balance
    }

    override fun getCustomerReport(clientToken: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): SboValue.PlayerRevenue {

        // type: agent or player
        val param = "getcustomerreport/${clientToken.appId}/user/${username}/type/player"
        val url = SboBuild.instance().build(token = clientToken, param = param)

        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerReportResult::class.java)
        this.checkCode(result.error.id)

        return result.playerRevenue.first()
    }

    override fun getCustomerBetList(clientToken: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<SboValue.PlayerBet> {

        val param = "getcustomerbetlist/${clientToken.appId}/user/${username}"
        val url = SboBuild.instance().build(token = clientToken, param = param)


        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerBetResult::class.java)
        this.checkCode(result.error.id)

        return result.playerBetList

    }
}