package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.MemberIntroduce
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import org.springframework.stereotype.Service

@Service
class MemberIntroduceServiceImpl : MemberIntroduceService {


    override fun get(memberId: Int): MemberIntroduce? {
        TODO("Not yet implemented")
    }

    override fun create(co: MemberIntroduceValue.MemberIntroduceCo) {
        TODO("Not yet implemented")
    }

    override fun update(uo: MemberIntroduceValue.MemberIntroduceUo) {
        TODO("Not yet implemented")
    }

    override fun list(query: MemberIntroduceValue.MemberIntroduceQuery): List<MemberIntroduce> {
        TODO("Not yet implemented")
    }

}