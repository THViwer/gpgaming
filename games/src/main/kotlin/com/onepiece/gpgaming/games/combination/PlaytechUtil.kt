package com.onepiece.gpgaming.games.combination

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

object PlaytechUtil {

    fun handle(file: File, language: Language): List<SlotGame> {
        return file.readLines().mapNotNull {
            val list = it.split(",")
            list.firstOrNull { it.isNotBlank() } ?.let {
                val gameId = list[0]
                val cname = list[1]
                val ename = list[2]
                if (list[3].contains("白虎")) {
                    println("xx")
                }
                val category = list[3].let { GameCategory.valueOf(it) }

                val name = if (language == Language.CN) cname else ename
                SlotGame(platform = Platform.PlaytechSlot, category = category, gameId = gameId, gameName = name, hot = false, new = false,
                        icon = "-", status = Status.Normal, touchIcon = "-")
            }
        }
    }

}

fun main() {

    val mobileFile = File("/Users/cabbage/Downloads/playtech_mobile_done.csv")
    val html5File = File("/Users/cabbage/Downloads/playtech_html5_done.csv")

    listOf(Language.CN, Language.EN)
            .forEach { language ->

                listOf(LaunchMethod.Wap, LaunchMethod.Web).forEach { launch ->

                    val file = if (launch == LaunchMethod.Wap) mobileFile else html5File


                    val games = PlaytechUtil.handle(html5File, language)

                    val hots = hashSetOf<String>()
                    val news = hashSetOf<String>()

                    val categories = games.map {
                        if (it.category == GameCategory.Hot) {
                            hots.add(it.gameId)
                        }
                        if (it.category == GameCategory.New) {
                            news.add(it.gameId)
                        }

                        val hot = hots.contains(it.gameId)
                        val new = news.contains(it.gameId)

                        it.copy(hot = hot, new = new)
                    }.groupBy { it.category }
                            .map { SlotCategory(gameCategory = it.key, games = it.value) }

                    val json = jacksonObjectMapper().writeValueAsString(categories)
                    val jsonFile = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
                    jsonFile.writeBytes(json.toByteArray())

                    AwsS3Util.uploadLocalFile(jsonFile, "slot/playtech_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json")

                    jsonFile.delete()

                }


            }

}