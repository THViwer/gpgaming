package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.games.GameReportApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.ReportVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class Kiss918GameReportApi(
        private val okHttpUtil: OkHttpUtil
) : GameReportApi {

    private val log = LoggerFactory.getLogger(Kiss918GameReportApi::class.java)

    override fun memberReport(username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo> {

        val url = Kiss918Builder.instance(path = "/ashx/AccountReport.ashx")
                .set("userName", username)
                .set("sDate", startDate.toString())
                .set("eDate", endDate.toString())
                .build(username = username)
        val result = okHttpUtil.doGet(url, Kiss918Value.ReportResult::class.java)
        log.info("member report: $result")

        return result.result.map {
            ReportVo(day = it.myDate, win = it.win)
        }

    }

    override fun clientReport(startDate: LocalDate, endDate: LocalDate): List<ReportVo> {

        val url = Kiss918Builder.instance(path = "/ashx/AgentMoneyLog.ashx")
                .set("userName", Kiss918Constant.AGENT_CODE)
                .set("sDate", startDate.toString())
                .set("eDate", endDate.toString())
                .build(username = Kiss918Constant.AGENT_CODE)
        val result = okHttpUtil.doGet(url, Kiss918Value.ReportResult::class.java)
        log.info("member report: $result")

        return result.result.map {
            ReportVo(day = it.myDate, win = it.win)
        }
    }
}