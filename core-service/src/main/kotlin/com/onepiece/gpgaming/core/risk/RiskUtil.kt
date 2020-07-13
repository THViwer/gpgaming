package com.onepiece.gpgaming.core.risk

import com.onepiece.gpgaming.beans.enums.RiskLevel
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.core.service.MemberService
import org.springframework.stereotype.Component

@Component
class RiskUtil(
        private val memberService: MemberService
) {


    /**
     * 检查并返回对应的风控等级
     */
    fun checkRiskLevel(clientId: Int, name: String, ip: String): RiskLevel {

        val sameName = this.isSameName(clientId = clientId, name = name)
        val sameIp = this.isSameRegisterIp(clientId = clientId, ip = ip)

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
    fun isSameName(clientId: Int, name: String): Boolean {
        val memberQuery = MemberQuery(clientId = clientId, name = name)
        val list = memberService.list(memberQuery)
        return list.isNotEmpty()
    }

    /**
     * 检测是否有相同的注册ip
     */
    fun isSameRegisterIp(clientId: Int, ip: String): Boolean {
        if (ip == "None" || ip == "admin:register" || ip == "agent:register" || ip == "localhost" || ip == "127.0.0.1") {
            return false
        }

        val memberQuery = MemberQuery(clientId = clientId, registerIp = ip)
        val list = memberService.list(memberQuery)
        return list.isNotEmpty()
    }


}