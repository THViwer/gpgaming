package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.beans.value.database.WalletDepositUo
import com.onepiece.gpgaming.beans.value.database.WalletFreezeUo
import com.onepiece.gpgaming.beans.value.database.WalletNoteCo
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.database.WalletTransferInUo
import com.onepiece.gpgaming.beans.value.database.WalletTransferOutUo
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.beans.value.database.WalletWithdrawUo
import com.onepiece.gpgaming.core.dao.WalletDao
import com.onepiece.gpgaming.core.dao.WalletNoteDao
import com.onepiece.gpgaming.core.email.EmailSMTPService
import com.onepiece.gpgaming.core.risk.VipUtil
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.utils.ApplicationVersion
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class WalletServiceImpl(
        private val walletDao: WalletDao,
        private val walletNoteDao: WalletNoteDao,
        private val emailSMTPService: EmailSMTPService
) : WalletService {

    private val log = LoggerFactory.getLogger(WalletServiceImpl::class.java)

    @Autowired
    lateinit var memberInfoService: MemberInfoService

    @Autowired
    lateinit var memberIntroduceService: MemberIntroduceService

    @Autowired
    lateinit var vipUtil: VipUtil

    @Autowired
    lateinit var clientConfigService: ClientConfigService

    @Autowired
    lateinit var memberService: MemberService

    override fun getMemberWallet(memberId: Int): Wallet {
        return walletDao.getMemberWallet(memberId)
    }

    override fun create(walletCo: WalletCo) {
        val state = walletDao.create(walletCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(walletUo: WalletUo, time: Int): BigDecimal {
        if (time > 3) error(OnePieceExceptionCode.DB_CHANGE_FAIL)

        return try {
            this.update(walletUo)
        } catch (e: Exception) {
            log.info("更新用户余额失败,memberId=${walletUo}", e)
            throw e
//            this.update(walletUo, time + 1)
        }
    }

    fun update(walletUo: WalletUo): BigDecimal {

        val wallet = this.getMemberWallet(walletUo.memberId)

        val state = when (walletUo.event) {

            WalletEvent.Rebate,
            WalletEvent.Commission,
            WalletEvent.INTRODUCE_DEPOSIT_COMMISSION,
            WalletEvent.INTRODUCE_REGISTER_COMMISSION -> {
                val walletDepositUo = WalletDepositUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.otherAddAmount(walletDepositUo)
            }
            WalletEvent.ThirdPay,
            WalletEvent.DEPOSIT -> {
                val walletDepositUo = WalletDepositUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.deposit(walletDepositUo)
            }
            WalletEvent.FREEZE -> {
                val walletFreezeUo = WalletFreezeUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.freeze(walletFreezeUo)
            }
            WalletEvent.WITHDRAW -> {
                val walletWithdrawUo = WalletWithdrawUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.withdraw(walletWithdrawUo)
            }
            WalletEvent.WITHDRAW_FAIL -> {
                val walletWithdrawUo = WalletWithdrawUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.withdrawFail(walletWithdrawUo)
            }
            WalletEvent.TRANSFER_IN, WalletEvent.Artificial -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
            WalletEvent.TRANSFER_IN_ROLLBACK -> {
                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = BigDecimal.ZERO)
                walletDao.transferOut(transferOutUo, 0)
            }
            WalletEvent.TRANSFER_OUT -> {
                check(wallet.balance >= walletUo.money) { OnePieceExceptionCode.BALANCE_NOT_WORTH }

                val transferOutUo = WalletTransferOutUo(id = wallet.id, processId = wallet.processId, money = walletUo.money, giftMoney = walletUo.giftBalance ?: BigDecimal.ZERO)
                walletDao.transferOut(transferOutUo, 1)
            }
            WalletEvent.TRANSFER_OUT_ROLLBACK -> {
                val transferInUo = WalletTransferInUo(id = wallet.id, processId = wallet.processId, money = walletUo.money)
                walletDao.transferIn(transferInUo)
            }
            WalletEvent.INTRODUCE -> {
                // NOTHING
                true
            }
        }

        val afterMoney: BigDecimal
        val money: BigDecimal
        when (walletUo.event) {
            WalletEvent.FREEZE -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance.minus(walletUo.money)
            }
            WalletEvent.Rebate,
            WalletEvent.Commission,
            WalletEvent.ThirdPay,
            WalletEvent.DEPOSIT,
            WalletEvent.WITHDRAW_FAIL,
            WalletEvent.TRANSFER_IN,
            WalletEvent.Artificial,
            WalletEvent.INTRODUCE_DEPOSIT_COMMISSION,
            WalletEvent.INTRODUCE_REGISTER_COMMISSION,
            WalletEvent.TRANSFER_OUT_ROLLBACK -> {
                money = walletUo.money
                afterMoney = wallet.balance.plus(walletUo.money)
            }
            WalletEvent.TRANSFER_IN_ROLLBACK,
            WalletEvent.TRANSFER_OUT -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance.minus(walletUo.money)
            }
            WalletEvent.INTRODUCE -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance
            }
            WalletEvent.WITHDRAW -> {
                money = walletUo.money.negate()
                afterMoney = wallet.balance
            }

        }
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 更新会员信息
        when (walletUo.event) {
            WalletEvent.DEPOSIT,
            WalletEvent.ThirdPay -> {
                val infoUo = MemberInfoValue.MemberInfoUo.ofDeposit(memberId = walletUo.memberId, amount = walletUo.money)
                memberInfoService.asyncUpdate(uo = infoUo)

                try {
                    val totalDeposit = wallet.totalDepositBalance.plus(money)
                    this.checkIntroduce(clientId = wallet.clientId, memberId = wallet.memberId, totalDeposit = totalDeposit)
                } catch (e: Exception) {
                    log.error("检查介绍失败, client_id = ${walletUo.clientId}, 参数：${walletUo}")
                }

                // 刷新vip等级
                try {
                    vipUtil.checkAndUpdateVip(clientId = walletUo.clientId, memberId = walletUo.memberId, amount = walletUo.money)
                } catch (e: Exception) {
                    log.error("刷新vip等级失败, client_id = ${walletUo.clientId}, 参数：${walletUo}")
                }

                // 加上已是首充
                val member = memberService.getMember(id = walletUo.memberId)
                if (!member.firstDeposit) {
                    val memberUo = MemberUo(id = member.id, firstDeposit = true, firstDepositDay = LocalDate.now())
                    memberService.update(memberUo = memberUo)

                    if (member.email != null && member.email != "") {
                        emailSMTPService.firstDepositSend(clientId = member.clientId, username = member.username, email = member.email!!)
                    }
                }


            }
            WalletEvent.WITHDRAW -> {
                val infoUo = MemberInfoValue.MemberInfoUo.ofWithdraw(memberId = walletUo.memberId, amount = walletUo.money)
                memberInfoService.asyncUpdate(uo = infoUo)
            }
            else -> {
            }
        }


        // TODO async insert wallet note
        val walletNoteCo = WalletNoteCo(clientId = walletUo.clientId, memberId = wallet.memberId, event = walletUo.event, remarks = walletUo.remarks,
                waiterId = walletUo.waiterId, eventId = walletUo.eventId, money = money, promotionMoney = walletUo.giftBalance, originMoney = wallet.balance,
                afterMoney = afterMoney)
        val wnState = walletNoteDao.create(walletNoteCo)
        check(wnState) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        return wallet.balance.plus(walletUo.money)
    }

    private fun checkIntroduce(clientId: Int, memberId: Int, totalDeposit: BigDecimal) {
        val memberInfo = memberInfoService.get(memberId = memberId)
        memberIntroduceService.get(memberId = memberId)?.let { introduce ->
            if (!introduce.depositActivity) {

                when (ApplicationVersion.checkIsNewVersion(clientId)) {
                    true -> { // 新的推荐奖励
                        if (totalDeposit.toDouble() >= ApplicationVersion.INTRODUCE_DEPOSIT_NEED_AMOUNT) {
                            val commission = ApplicationVersion.INTRODUCE_REGISTER_DEPOSIT_COMMISSION
                            val walletUo = WalletUo(clientId = clientId, waiterId = null, memberId = introduce.introduceId, money = commission, giftBalance = null,
                                    eventId = null, event = WalletEvent.INTRODUCE_DEPOSIT_COMMISSION, remarks = "introduce deposit commission")
                            this.update(walletUo)

                            val introduceUo = MemberIntroduceValue.MemberIntroduceUo(id = introduce.id, depositActivity = true, registerActivity = null,
                                    introduceCommission = commission)
                            memberIntroduceService.update(introduceUo)
                        }
                    }
                    else -> { // 老的推荐奖励
                        val config = clientConfigService.get(clientId = clientId)

                        val endDay = memberInfo.createdTime.plusDays(config.commissionPeriod.toLong())
                        if (endDay >= LocalDateTime.now() && memberInfo.totalDeposit.toDouble() > config.depositPeriod.toDouble()) {

                            val commission = config.depositCommission

                            val walletUo = WalletUo(clientId = clientId, waiterId = null, memberId = introduce.memberId, money = commission, giftBalance = null,
                                    eventId = null, event = WalletEvent.INTRODUCE_DEPOSIT_COMMISSION, remarks = "introduce deposit commission")
                            this.update(walletUo)

                            val myWalletUo = walletUo.copy(memberId = introduce.introduceId)
                            this.update(myWalletUo)

                            val introduceUo = MemberIntroduceValue.MemberIntroduceUo(id = introduce.id, depositActivity = true, registerActivity = null,
                                    introduceCommission = config.depositCommission)
                            memberIntroduceService.update(introduceUo)
                        }
                    }
                }
            }
        }
    }

    override fun query(walletQuery: WalletQuery): List<Wallet> {
        return walletDao.query(walletQuery)
    }
}