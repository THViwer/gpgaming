package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.internet.web.SlotCategory
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.utils.AwsS3Util
import java.io.File
import java.util.*


object TTGUtil {

    fun handle(launch: LaunchMethod, language: Language): List<SlotGame> {
        val file = File("/Users/cabbage/Downloads/ttg_${launch.name.toLowerCase()}_done.csv")

        return file.readLines().map { line ->
            val data = line.split(",")
            val gameId = data[0]

            if (gameId.isNotBlank()) {
                val gameEnglinshName = data[1]
                val gameChineseName = data[2]
                val category = data[4].let { GameCategory.valueOf(it) }

                val gameName = if (language == Language.CN) gameChineseName else gameEnglinshName

                val icon = "http://ams-games.stg.ttms.co/player/assets/images/games/${gameId.split(":").first()}.png"
                SlotGame(gameId = gameId, gameName = gameName, category = category, platform = Platform.TTG, hot = false, new = false, icon = icon, touchIcon = null, status = Status.Normal)
            } else null
        }.filterNotNull()

    }
}

fun main() {

    listOf(LaunchMethod.Web, LaunchMethod.Wap).forEach { launch ->

        listOf(Language.EN, Language.CN).forEach { language ->

            val hots = hashSetOf<String>()
            val news = hashSetOf<String>()
            val games = TTGUtil.handle(launch, language).map {
                if (it.category == GameCategory.Hot) {
                    hots.add(it.gameId)
                }
                if (it.category == GameCategory.New) {
                    news.add(it.gameId)
                }

                it.copy(hot = hots.contains(it.gameId), new = news.contains(it.gameId))
            }

            val categories = games.groupBy { it.category }.map {
                SlotCategory(gameCategory = it.key, games = it.value)
            }

            val json = jacksonObjectMapper().writeValueAsString(categories)
            val file = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
            file.writeBytes(json.toByteArray())

            AwsS3Util.uploadLocalFile(file, "slot/ttg_${launch}_$language.json".toLowerCase())

            file.delete()
        }
    }


}


//fun main() {
//
//    val file = File("/Users/cabbage/Downloads/ttg_wap.Done.csv")
//
//    val file2 = File("//Users/cabbage/Desktop/TTG GAMES LIST NOV 2019.csv")
//
//    val map = file2.readLines().map { line ->
//        val data = line.split(",")
//        val gameId = data[3]
//        val name = data[4]
//        val type = data[5]
//        val lauhchs = data[9]
//        "$gameId${name}$type" to lauhchs
//    }.toMap()
//
//    val csvData = file.readLines().map {
//        val id = it.split(",").first()
//        it.plus(",${map[id]}")
//    }
//
//
//    val file3 = File("/Users/cabbage/Downloads/ttg_wap2.Done.csv")
//
//
//}
