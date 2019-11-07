package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.ClientAuthVo
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class Kiss918GameOrderApi(
        private val okHttpUtil: OkHttpUtil
) : GameOrderApi {

    override fun synOrder(clientAuthVo: ClientAuthVo?, startTime: LocalDateTime, endTime: LocalDateTime): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    override fun report(clientAuthVo: ClientAuthVo?, startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report> {
        return emptyList()
    }

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override fun query(clientAuthVo: ClientAuthVo?, query: BetOrderValue.Query): Any {
        val url = Kiss918Builder.instance(Kiss918Constant.API_ORDER_URL, path = "/ashx/GameLog.ashx")
                .set("pageIndex", "1")
                .set("pageSize", "1000")
                .set("userName", query.username)
                .set("sDate", query.startTime.format(dateTimeFormatter))
                .set("eDate", query.endTime.format(dateTimeFormatter))
                .build(username = query.username)

        val result = okHttpUtil.doGet(url, String::class.java)

        return result;
    }
}