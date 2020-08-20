package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberIntroduce
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MemberIntroduceDao : BasicDao<MemberIntroduce> {

    fun create(co: MemberIntroduceValue.MemberIntroduceCo): Boolean

    fun update(uo: MemberIntroduceValue.MemberIntroduceUo): Boolean

    fun getByMemberId(memberId:  Int): MemberIntroduce?

    fun list(query: MemberIntroduceValue.MemberIntroduceQuery): List<MemberIntroduce>

}