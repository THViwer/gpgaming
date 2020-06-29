package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MemberInfo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue

interface MemberInfoService {

    fun create(co: MemberInfoValue.MemberInfoCo)

    fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo>

    fun asyncUpdate(uo: MemberInfoValue.MemberInfoUo)
}