package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import java.math.BigDecimal
import java.time.LocalDate

interface MemberDailyReportDao  {

    fun create(reports: List<MemberDailyReport>)

    fun total(query: MemberReportQuery): MemberReportValue.MemberReportTotal

    fun query(query: MemberReportQuery): List<MemberDailyReport>

    fun queryRebate(current: Int, size: Int): List<MemberDailyReport>

    fun updateRebate(ids: List<Int>)

    fun rebate(startDate: LocalDate): Map<Int, BigDecimal>

    fun analysis(query: MemberReportValue.AnalysisQuery): List<MemberReportValue.AnalysisVo>

    fun collect(query: MemberReportValue.CollectQuery): List<MemberReportValue.MemberMonthReport>

    fun saleCollect(query: MemberReportValue.MemberCollectQuery): List<MemberReportValue.SaleReportVo>

    fun markCollect(day: LocalDate): List<MarketDailyReportValue.MarketDailyReportCo>

}