package com.onepiece.treasure.games.cta666

import com.onepiece.treasure.games.GameOrderApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class Cat666GameOrderApi(
        private val okHttpUtil: OkHttpUtil
): GameOrderApi {

    override fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String {

        val param = Cat666ParamBuilder.instance("getReport")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}"
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cat666Result.Report::class.java)
        Cat666Constant.checkCode(result.codeId)

        val ids = result.list.map { it.id }
        this.mark(ids)

        return UUID.randomUUID().toString()
    }

    private fun mark(ids: List<Long>) {

        val list = ids.joinToString(separator = ",")
        val param = Cat666ParamBuilder.instance("mark")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "list":[$list]
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cat666Result.Mark::class.java)
        Cat666Constant.checkCode(result.codeId)

    }
}