package com.onepiece.treasure.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.utils.AwsS3Util
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class SlotGameTask(
        private val objectMapper: ObjectMapper,
        private val gameApi: GameApi
)  {

//     @Scheduled(cron="0/10 * *  * * ? ")
    fun jokerGameTask() {

        val webGames = gameApi.slotGames(clientId = 1, platform = Platform.Joker, launch = LaunchMethod.Web).groupBy { it.category }.map {
            SlotCategory(gameCategory = it.key, games = it.value)
        }
        this.upload(games = webGames, path = "slot/joker_web.json")

        val wapGames = gameApi.slotGames(clientId = 1, platform = Platform.Joker, launch = LaunchMethod.Web).groupBy { it.category }.map {
            SlotCategory(gameCategory = it.key, games = it.value)
        }
        this.upload(games = wapGames, path = "slot/joker_web.json")

    }

    private fun upload(games: List<SlotCategory>, path: String) {
        if (games.isEmpty()) return

        val webJson = objectMapper.writeValueAsString(games)
        val file = File("~/${UUID.randomUUID()}.json")
        file.writeBytes(webJson.toByteArray())
        AwsS3Util.uploadLocalFile(file = file, name = path)
        file.delete()
    }

}