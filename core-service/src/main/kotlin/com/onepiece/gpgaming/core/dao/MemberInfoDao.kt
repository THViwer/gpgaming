package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberInfo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MemberInfoDao: BasicDao<MemberInfo> {

    fun has(memberId: Int): MemberInfo?

    fun create(co: MemberInfoValue.MemberInfoCo): Boolean

    fun update(uo: MemberInfoValue.MemberInfoUo): Boolean

    fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo>

    fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int)

}