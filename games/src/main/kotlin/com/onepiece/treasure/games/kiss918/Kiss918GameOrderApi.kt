package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class Kiss918GameOrderApi(
        private val okHttpUtil: OkHttpUtil
) : GameOrderApi {

    override fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report> {
        return emptyList()
    }

    override fun query(query: BetOrderValue.Query): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}