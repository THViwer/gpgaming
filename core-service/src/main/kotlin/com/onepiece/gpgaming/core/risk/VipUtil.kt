package com.onepiece.gpgaming.core.risk

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.service.DepositService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PayOrderService
import com.onepiece.gpgaming.core.service.VipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class VipUtil(
        private val depositService: DepositService,
        private val payOrderService: PayOrderService,
        private val vipService: VipService
) {

    @Autowired
    lateinit var memberService: MemberService


    fun checkAndUpdateVip(clientId: Int, memberId: Int, amount: BigDecimal = BigDecimal.ZERO) {

        val levels = vipService.list(clientId = clientId)
                .filter { it.status == Status.Normal }
                .sortedBy { it.depositAmount }
        if (levels.isEmpty()) return

        levels.firstOrNull {

            val needDeposit= it.depositAmount

            val day = it.days.substring(0, it.days.length).toLong()
            val unit = it.days.substring(it.days.length -1, it.days.length)

            val endDate = LocalDate.now()

            val startDate = when (unit) {
                "d" -> endDate.minusDays(day)
                "w" -> endDate.minusWeeks(day)
                "m" -> endDate.minusMonths(day)
                "y" -> endDate.minusYears(day)
                else -> endDate.minusDays(day)
            }

            val depositAmount = depositService.sumSuccessful(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)
            val payAmount = payOrderService.sumSuccessful(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)

            depositAmount.plus(payAmount).plus(amount).toDouble() >= needDeposit.toDouble()
        }?.also {

            val member = memberService.getMember(id = memberId)

            if (it.levelId != member.levelId) {
                val levelId = it.levelId

                val memberUo = MemberUo(id = memberId, levelId = levelId, vipId = it.id)
                memberService.update(memberUo)
            }
        }

    }


}