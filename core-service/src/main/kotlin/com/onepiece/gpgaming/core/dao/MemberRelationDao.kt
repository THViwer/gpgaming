package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberRelation
import com.onepiece.gpgaming.beans.value.database.MemberRelationValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MemberRelationDao: BasicDao<MemberRelation> {

    fun create(co: MemberRelationValue.MemberRelationCo): Boolean

}