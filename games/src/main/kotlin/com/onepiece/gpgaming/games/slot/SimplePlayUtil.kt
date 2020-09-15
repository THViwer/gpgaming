package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.utils.AwsS3Util
import java.io.File
import java.util.stream.Collectors

object SimplePlayUtil {

    fun upload(imageFiles: File, gameId: String, language: Language, path: String): String? {

        val file = imageFiles.listFiles().firstOrNull { it.name.contains(gameId) }?: return null

        val iconFiles = File(file, "Game Icon/$path/")

        if (gameId == "EG-SLOT-A030") {
            println(1)
        }

        return try {
            val icon = iconFiles.listFiles().firstOrNull { it.name.toLowerCase().contains("${language.name.toLowerCase()}.jpg") }

            icon?.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun execute(file: File, imageFiles: File,  language: Language): List<SlotGame> {

        return file.readLines().parallelStream().filter { it.split(",").firstOrNull() != null } .map { line ->

            try {
                val data = line.split(",")

                val gameId = data[0]
                val ename = data[1]
                val cname = data[2]
                val cetegory = data[3].let { GameCategory.valueOf(it) }

                val name = listOf(
                        "530x328",
                        "564x215",
                        "530x238"
                ).map { this.upload(imageFiles = imageFiles, gameId = gameId, language = language, path = it) }.first { it != null }

                if (name == null) {
                    println("gameId = $gameId 无法查询到图片信息")
                }
                val imageFile = File(name!!)
                val iconPath = AwsS3Util.uploadLocalFile(imageFile, "slot/simple_play/${gameId}_${language.name.toLowerCase()}.jpg")

                val gameName = if (language == Language.CN) cname else ename
                SlotGame(gameId = gameId, platform = Platform.SimplePlay, category = cetegory, gameName = gameName,
                        icon = iconPath, touchIcon = null, hot = false, new = false, status = Status.Normal)
            } catch (e: Exception) {
                println("$line 不能上传图片")
                null
            }
        }.filter { it != null }.map { it!! }.collect(Collectors.toList())
    }
}
//
//fun main() {
//    val imageFiles = File("/Users/cabbage/Downloads/SP - Slot Games Art Work")
//
//    val csvFile = File("/Users/cabbage/Desktop/simple_game_done.csv")
//
//
//    listOf(Language.EN, Language.CN).forEach { language ->
//
//        val games = SimplePlayUtil.execute(file = csvFile, imageFiles = imageFiles, language = language)
//
//        val hots = hashSetOf<String>()
//        val news = hashSetOf<String>()
//
//        val categories = games.map {
//            if (it.category == GameCategory.Hot) {
//                hots.add(it.gameId)
//            }
//            if (it.category == GameCategory.New) {
//                news.add(it.gameId)
//            }
//
//            val hot = hots.contains(it.gameId)
//            val new = news.contains(it.gameId)
//
//            it.copy(hot = hot, new = new)
//        }.groupBy { it.category }
//                .map { SlotCategory(gameCategory = it.key, games = it.value) }
//
//        val json = jacksonObjectMapper().writeValueAsString(categories)
//        val jsonFile = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
//        jsonFile.writeBytes(json.toByteArray())
//
//        AwsS3Util.uploadLocalFile(jsonFile, "slot/simple_play_${language.name.toLowerCase()}.json")
//
//        jsonFile.delete()
//    }
//
//
//}