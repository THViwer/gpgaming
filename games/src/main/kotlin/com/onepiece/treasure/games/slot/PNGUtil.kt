package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import java.io.File

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


    fun execute(launch: LaunchMethod): List<SlotGame> {

        val filePath = "/Users/cabbage/workspace/onepiece/treasure/gamefile/png_game_doen.csv"
        val file = File(filePath)

        return file.readLines().mapNotNull {

            val lines = it.split(",")
            if (lines.first().isBlank()) {
                null
            } else {
                lines
            }

        }.map {
            val englishGameName = it[0]
            val chineseGameName = it[1]
            val desktopGameId = it[2]
            val mobileGameId = it[3]
            val desktopGid = it[4]
            val mobileGid = it[5]
            val category = it[6].let { GameCategory.valueOf(it) }
//            val icon = "https://s3.ap-southeast-1.amazonaws.com/awspg1/slot/$game"
            val gameId = if (launch == LaunchMethod.Web) desktopGid else mobileGid
            SlotGame(Platform.PNG, gameId = gameId, category = category, gameName = englishGameName, chineseGameName = chineseGameName,
                    icon = "", touchIcon = "", hot = false, new = false, status = Status.Normal)
        }

    }


}

fun main() {

    val tmpGames = PNGUtil.execute(launch = LaunchMethod.Web)

    PNGUtil.uploadIcon(tmpGames)
}