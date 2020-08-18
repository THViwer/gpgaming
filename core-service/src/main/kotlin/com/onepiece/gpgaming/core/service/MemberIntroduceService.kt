package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MemberIntroduce
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue

interface MemberIntroduceService {

    fun get(memberId: Int): MemberIntroduce?

    fun create(co: MemberIntroduceValue.MemberIntroduceCo)

    fun update(uo: MemberIntroduceValue.MemberIntroduceUo)

    fun list(query: MemberIntroduceValue.MemberIntroduceQuery): List<MemberIntroduce>

}