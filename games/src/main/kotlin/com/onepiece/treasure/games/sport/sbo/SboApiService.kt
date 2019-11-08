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

    override fun registerPlayer(token: DefaultClientToken, username: String): String {

        val platformUsername = "${token.appId}${username}"

        val param = "registerplayer/${token.appId}/player/${platformUsername}/agent/${token.appId}/lang/${language}"
        val url = SboBuild.instance().build(token = token, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboDefaultResult::class.java)
        this.checkCode(result.error.id)

        return platformUsername
    }

    override fun login(token: DefaultClientToken, username: String): String {
        val param = "login/${token.appId}/player/${username}"
        val url = SboBuild.instance().build(token = token, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboLoginResult::class.java)
        this.checkCode(result.error.id)

        return "${GameConstant.SBO_START_URL}?token=${result.token}"
    }

    override fun depositOrWithdraw(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String {
        return when {
            amount.toDouble() > 0 -> {
                val param = "deposit/${token.appId}/player/${username}/amount/${amount}/txnid/${orderId}"
                val url = SboBuild.instance().build(token = token, param = param)

                val result = okHttpUtil.doGet(url, SboValue.SboDepositResult::class.java)
                this.checkCode(result.error.id)

                result.refno
            }
            else -> {
                // 检查取款金额是否足够
                val balance = this.getPlayerBalance(token = token, username = username)
                check(balance.toDouble() >= amount.negate().toDouble()) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

                // 如果amount = full 则是取全部
                val param = "withdrawal/${token.appId}/player/${username}/amount/${amount.negate()}/txnid/${orderId}"
                val url = SboBuild.instance().build(token = token, param = param)
                val result = okHttpUtil.doGet(url, SboValue.SboWithdrawResult::class.java)
                this.checkCode(result.error.id)

                result.refno
            }
        }
    }

    override fun checkTransferStatus(token: DefaultClientToken, orderId: String): Boolean {
        val param = "checktransactionstatus/${token.appId}/txnid/${orderId}"
        val url = SboBuild.instance().build(token = token, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboCheckTransferStatusResult::class.java)

        return result.error.id == 0
    }

    override fun getPlayerBalance(token: DefaultClientToken, username: String): BigDecimal {

        val param = "getplayerbalance/${token.appId}/player/${username}"
        val url = SboBuild.instance().build(token = token, param = param)

        val result = okHttpUtil.doGet(url, SboValue.SboPlayerBalanceResult::class.java)
        this.checkCode(result.error.id)

        return result.balance
    }

    override fun getCustomerReport(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): SboValue.PlayerRevenue {

        // type: agent or player
        val param = "getcustomerreport/${token.appId}/user/${username}/type/player"
        val url = SboBuild.instance().build(token = token, param = param)

        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerReportResult::class.java)
        this.checkCode(result.error.id)

        return result.playerRevenue.first()
    }

    override fun getCustomerBetList(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<SboValue.PlayerBet> {

        val param = "getcustomerbetlist/${token.appId}/user/${username}"
        val url = SboBuild.instance().build(token = token, param = param)


        val result = okHttpUtil.doGet("${url}&startdate=${startDate}&enddate=${endDate}", SboValue.CustomerBetResult::class.java)
        this.checkCode(result.error.id)

        return result.playerBetList

    }
}