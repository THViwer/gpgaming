package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberTraceLog
import com.onepiece.gpgaming.beans.value.database.MemberTraceLogValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MemberTraceLogDao: BasicDao<MemberTraceLog> {

    fun create(co: MemberTraceLogValue.MemberTraceLogCo): Boolean

    fun list(query: MemberTraceLogValue.MemberTraceLogQuery): List<MemberTraceLog>

}