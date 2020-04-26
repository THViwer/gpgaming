package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.value.database.MemberValue
import java.time.LocalDate

interface AnalysisDao  {

    fun analysis(startDate: LocalDate, endDate: LocalDate, clientId: Int, memberIds: List<Int>?, sort: MemberAnalysisSort, size: Int): List<MemberValue.AnalysisData>


}