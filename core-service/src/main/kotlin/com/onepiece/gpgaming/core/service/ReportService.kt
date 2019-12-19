package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
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