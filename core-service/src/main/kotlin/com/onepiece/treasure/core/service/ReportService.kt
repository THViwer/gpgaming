package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.model.MemberPlatformDailyReport
import java.time.LocalDate

interface ReportService {

    /**
     * 会员平台报表
     */
    fun startMemberPlatformDailyReport(memberId: Int?, startDate: LocalDate): List<MemberPlatformDailyReport>

    /**
     * 会员报表
     */
    fun startMemberReport(memberId: Int?, startDate: LocalDate): List<MemberDailyReport>

    /**
     * 厅主平台报表
     */
    fun startClientPlatformReport(clientId: Int?, startDate: LocalDate): List<ClientPlatformDailyReport>

    /**
     * 厅主报表
     */
    fun startClientReport(clientId: Int?, startDate: LocalDate):List<ClientDailyReport>

}