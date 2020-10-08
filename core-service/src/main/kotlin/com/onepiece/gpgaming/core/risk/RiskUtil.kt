package com.onepiece.gpgaming.core.risk

import com.onepiece.gpgaming.beans.enums.RiskLevel
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class RiskUtil{

    @Autowired
    @Lazy
    lateinit var memberService: MemberService


    /**
     * 检查并返回对应的风控等级
     */
    fun checkRiskLevel(clientId: Int, username: String, name: String, ip: String): RiskLevel {
        return this.getRiskLevel(clientId = clientId, username = username, name = name, ip = ip)
    }

    fun getRiskLevel(clientId: Int, username: String, name: String, ip: String, async: Boolean = false): RiskLevel {
        val sameNames = this.isSameName(clientId = clientId, username = username, name = name)
        val sameIps = this.isSameRegisterIp(clientId = clientId, username = username, ip = ip)

        val sameName = sameNames.isNotEmpty()
        val sameIp = sameIps.isNotEmpty()

        if (async) {
            val members = sameNames.plus(sameIps).distinctBy { it.id }
            this.asyncRiskLevel(username = username, members = members)
        }

        return when {
            sameName && sameIp -> RiskLevel.Cyan
            sameIp -> RiskLevel.Pink
            sameName -> RiskLevel.Blue
            else -> RiskLevel.None
        }
    }

    /**
     * 检查是否有相同的用户名
     */
    fun isSameName(clientId: Int, username: String, name: String): List<Member> {
        val memberQuery = MemberQuery(clientId = clientId, name = name)
        return memberService.list(memberQuery)
    }

    /**
     * 检测是否有相同的注册ip
     */
    fun isSameRegisterIp(clientId: Int, username: String, ip: String): List<Member> {
        if (ip == "None" || ip == "admin:register" || ip == "agent:register" || ip == "localhost" || ip == "127.0.0.1") {
            return emptyList()
        }

        val memberQuery = MemberQuery(clientId = clientId, registerIp = ip)
        return memberService.list(memberQuery)
    }

    @Async
    fun asyncRiskLevel(username: String, members: List<Member>) {
        members.filter { it.username != username }.map {
            val risk = this.getRiskLevel(clientId = it.clientId, username = username, name = it.name, ip = it.registerIp, async = true)
            if (it.riskLevel != risk) {
                val memberUo = MemberUo(id = it.id, riskLevel = risk)
                memberService.update(memberUo = memberUo)
            }
        }
    }


}