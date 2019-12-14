package com.onepiece.treasure.games.slot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.utils.AwsS3Util
import java.io.File
import java.util.stream.Collectors

object GamePlayUtil {

    fun handle(file: File, language: Language): List<SlotGame> {

        val lines = file.readLines()
        val games = lines.parallelStream().map { line ->
            val data = line.split(",")
            val gameName = data[0]
            val chineseName = data[1]
            val gameId = data[2]

            val imageRootFile = if (gameName == "Vikings: Mega Reels") {
                File("/Users/cabbage/Downloads/R Slot Done/Vikings Mega Reels")
            } else {
                File("/Users/cabbage/Downloads/R Slot Done/${gameName}")
            }
            if (imageRootFile.exists()) {

                val imageFile = imageRootFile.listFiles().firstOrNull { file ->
                    when  {
                        file.name.contains(gameName) && file.name.contains("$language.png") -> true
                        file.name.contains(gameName) && file.name.contains("EN.png") -> true
                        file.name.contains(gameName) && file.name.contains("EN-CN.png") -> true
                        gameName == "Qi Xi"  && file.name == "Qixi Festival thumbnail $language.png" -> true
                        gameName == "Under Water World"  && file.name == "Underwater World thumbnail EN.png" -> true
                        gameName == "4 Guardians"  && file.name == "Four Guardians thumbnail $language.png" -> true
                        gameName == "Fu Lu Shou"  && file.name == "Fu Lu Shou thumbnail.png" -> true
                        gameName == "Four Beauties"  && file.name == "Four Beauties thumbnail.png" -> true
                        gameName == "Wuxia Princess Mega Reels"  && file.name == "Wuxia Princess - Mega Reels thumbnail $language.png" -> true
                        gameName == "Vikings: Mega Reels"  && file.name == "Vikings - Mega Reels thumbnail $language.png" -> true
                        gameName == "Strip' n Roll"  && file.name == "Strip'n Roll thumbnail $language.png" -> true
                        gameName == "Xuan Wu Blessing"  && file.name == "Xuan-Wu-Blessing thumbnail.png" -> true
                        else -> false
                    }
                }
//            val enImageFile = File("/Users/cabbage/Downloads/R Slot Done/${gameName}/$gameName thumbnail EN.png")
//            val cnImageFile = File("/Users/cabbage/Downloads/R Slot Done/${gameName}/$gameName thumbnail CN.png")
//
//            val imageFile = when {
//                language == Language.CN && cnImageFile.exists() -> cnImageFile
//                else -> enImageFile
//            }


                val name = when (language) {
                    Language.CN -> chineseName
                    else -> gameName
                }

                if (imageFile != null) {
                    val iconUrl = AwsS3Util.uploadLocalFile(imageFile, "slot/gameplay/${gameName.replace(" ", "").replace(":", "")}.png")
                    SlotGame(platform = Platform.GamePlay, gameId = gameId, category = GameCategory.Slot, gameName = name, chineseGameName = chineseName, hot = false, icon = iconUrl, new = false,
                            status = Status.Normal, touchIcon = null)
                } else {
                    println("游戏：$gameName 图片不存在")
                    null
                }
            } else {
                println("游戏：$gameName 图片不存在")
                null
            }
        }.collect(Collectors.toList()).filterNotNull()
        return games
    }

}

fun main() {

    listOf(Language.EN, Language.CN).forEach { language ->
        val file = File("/Users/cabbage/Downloads/game_play_done.csv")
        val games = GamePlayUtil.handle(file, Language.EN)

        val categories = games.groupBy { it.category }.map {
            SlotCategory(gameCategory =  it.key, games = it.value)
        }

        val objectMapper = jacksonObjectMapper()
        val json = objectMapper.writeValueAsString(categories)

        val jsonFile = File("/Users/cabbage/Desktop/gameplay_${language.toString().toLowerCase()}.json")
        jsonFile.writeBytes(json.toByteArray())

        AwsS3Util.uploadLocalFile(jsonFile, "slot/gameplay_${language.toString().toLowerCase()}.json")
    }
    //
}

