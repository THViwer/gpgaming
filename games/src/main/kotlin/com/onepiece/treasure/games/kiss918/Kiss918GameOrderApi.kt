package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class Kiss918GameOrderApi(
        private val okHttpUtil: OkHttpUtil
) : GameOrderApi {

    override fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}