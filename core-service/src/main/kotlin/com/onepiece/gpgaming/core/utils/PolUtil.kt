package com.onepiece.gpgaming.core.utils

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
open class PolUtil(
        private val redisService: RedisService,
        private val platformBindService: PlatformBindService
) {

    data class PullOrderLog(
            val clientId: Int,
            val platform: Platform,
            val executeTime: LocalDateTime,
            val flag: Boolean,
            val response: String
    )

    fun pol(log: PullOrderLog) {
        try {
            val redisKey = OnePieceRedisKeyConstant.getPOL(clientId = log.clientId, platform = log.platform)
            redisService.put(redisKey, log)
        } catch (e: Exception) {

        }
    }

    fun getPol(clientId: Int): List<PullOrderLog> {
        return try {
            platformBindService.findClientPlatforms(clientId = clientId).map { bind ->
                val redisKey = OnePieceRedisKeyConstant.getPOL(clientId = bind.clientId, platform = bind.platform)

                redisService.get(redisKey, PullOrderLog::class.java)
                        ?: PullOrderLog(clientId = bind.clientId, platform = bind.platform, executeTime = LocalDateTime.MIN,
                                flag = false, response = "never pull order")

            }
        } catch (e: Exception) {
            emptyList()
        }
    }

}