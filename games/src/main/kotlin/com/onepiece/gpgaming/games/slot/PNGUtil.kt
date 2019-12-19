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
import java.util.stream.Collectors

object PNGUtil {

    fun uploadIcon(games: List<SlotGame>) {
        val iconPath = File("/Users/cabbage/Downloads/游戏图标")

        val map = games.map { it.gameName to it }.toMap()
        val imageNames = iconPath.listFiles().map { it.name }

        games.map { game ->
            if (game.gameName == "BlackJack MH") {
                println("ss")
            }

            val icon = imageNames.find { it.toLowerCase().contains(game.gameName.toLowerCase()) }?: ""

            game.copy(icon = icon)
        }.filter { it.icon.isBlank() }.forEach {
            println(it)
        }
    }


    fun execute(launch: LaunchMethod, language: Language): List<SlotGame> {

        val filePath = "/Users/cabbage/workspace/onepiece/treasure/gamefile/png_game_doen.csv"
        val file = File(filePath)

        val iconPath = "/Users/cabbage/Downloads/中文图标"

        return file.readLines().mapNotNull {

            val lines = it.split(",")
            if (lines.first().isBlank()) {
                null
            } else {
                lines
            }

        }.parallelStream().map {
            val englishGameName = it[0]
            val chineseGameName = it[1]
            val desktopGameId = it[2]
            val mobileGameId = it[3]
            val desktopGid = it[4]
            val mobileGid = it[5]
            val category = it[6].let { GameCategory.valueOf(it) }
//            val icon = "https://s3.ap-southeast-1.amazonaws.com/awspg1/slot/$game"


            val gameName = if (language == Language.CN) chineseGameName else englishGameName
            val iconFile = File("$iconPath/$chineseGameName.png")
            if (iconFile.exists()) {
                val path = AwsS3Util.uploadLocalFile(iconFile, "slot/png/${englishGameName.replace(" ", "").replace("'", "").replace(":", "")}.png")

                val gameId = if (launch == LaunchMethod.Web) desktopGid else mobileGid
                SlotGame(Platform.PNG, gameId = gameId, category = category, gameName = gameName,
                        icon = path, touchIcon = path, hot = false, new = false, status = Status.Normal)
            } else {
                null
            }
        }.collect(Collectors.toList()).filterNotNull()

    }


}

fun main() {

    val objectMapper = jacksonObjectMapper()

    listOf(LaunchMethod.Wap, LaunchMethod.Web).forEach { launch ->

        listOf(Language.EN, Language.CN).forEach { language ->

            val tmpGames = PNGUtil.execute(launch = launch, language = language)

            val hots = arrayListOf<String>()
            val news = arrayListOf<String>()
            val categories = tmpGames.map {

                if (it.category == GameCategory.Hot) {
                    hots.add(it.gameId)
                } else if (it.category == GameCategory.New) {
                    news.add(it.gameId)
                }

                val hot = hots.contains(it.gameId)
                val new = news.contains(it.gameId)

                it.copy(hot = hot, new = new)

            }.groupBy { it.category }.map {
                SlotCategory(gameCategory = it.key, games = it.value)
            }

            val json = objectMapper.writeValueAsString(categories)

            val file = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
            file.writeBytes(json.toByteArray())

            AwsS3Util.uploadLocalFile(file, "slot/png_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json")

            file.delete()
        }

    }



//    PNGUtil.uploadIcon(tmpGames)
}