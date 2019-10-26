package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.model.Member
import com.onepiece.treasure.beans.value.database.LoginValue
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo

interface MemberService {

    fun getMember(id: Int): Member

    fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member>

    fun login(loginValue: LoginValue): Member

    fun create(memberCo: MemberCo)

    fun update(memberUo: MemberUo)

}