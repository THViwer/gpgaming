@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.onepiece.treasure.games.slot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.beans.SystemConstant
import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.games.SlotMenuUtil
import com.onepiece.treasure.utils.AwsS3Util
import java.io.File
import java.util.*

object MicroGamingUtil {

    val csv = ""

//    val games: List<SlotGame>
//    init {
//
//        games = csv.lines().map {
//            this.handlerLines(it)
//        }.filterNotNull()
//    }

    fun handlerLines(line: String, iconPath: String): SlotGame? {
        val list = line.split(",")
        val gameName = list[0]
        val chineseGameName = list[1]
        val flashGameId = list[2]
        val flashAppId = list[3]
        val html5GameId = list[4]
        val html5AppId = list[5]

        val category = list[14]
        val imageList = list[23]
        if (imageList.isBlank() || (flashGameId.isBlank() && html5GameId.isBlank())) return null

        val gameId = if (html5GameId.isNotBlank()) {
            "${html5GameId}_${html5AppId}"
        } else {
            "${flashGameId}_${flashAppId}"
        }
//
//        val gameCategory = when (category) {
//            "Bonus Slot" -> GameCategory.SLOT
//            "Feature Slot" -> GameCategory.SLOT
//            "Classic Slot" -> GameCategory.SLOT
//            "Video Poker" -> GameCategory.VideoPoker
//            "Video Slot" -> GameCategory.SlotVideo
//            "Table" -> GameCategory.Baccarat
//            "Others" -> GameCategory.Default
//            else -> GameCategory.Default
//        }

        val imageName = imageList.split("/").first().split(".").first().replace(" ", "").replace("®", "")

        // 上传图片
        val originIcon = File("$iconPath/${imageName}.png")
        AwsS3Util.uploadLocalFile(originIcon, "slot/micro_game/${imageName}.png")

        // 切分图片
        val cupPath = "$iconPath/123"
        val fileList = MicroGamingCutUtil.cutImageToFile(originIcon, cupPath, 2)

        val icon: String
        val touchIcon: String
        if (fileList.size == 1) {
            icon = "${SystemConstant.AWS_SLOT}/micro_game/${imageName}.png"
            touchIcon = "${SystemConstant.AWS_SLOT}/micro_game/${imageName}.png"
        } else {
            AwsS3Util.uploadLocalFile(fileList[0], "slot/micro_game/${imageName}_0.png")
            AwsS3Util.uploadLocalFile(fileList[1], "slot/micro_game/${imageName}_1.png")


            icon = "${SystemConstant.AWS_SLOT}/micro_game/${imageName}_0.png"
            touchIcon = "${SystemConstant.AWS_SLOT}/micro_game/${imageName}_1.png"
        }

        return SlotGame(gameId = gameId, gameName = gameName, chineseGameName = chineseGameName, category = GameCategory.Slot, icon = icon, touchIcon = touchIcon,
                hot = false, new = false, status = Status.Normal, platform = Platform.MicroGaming)
    }

    fun uploadJson(csvFile: String, iconPath: String) {
        // 生成json格式
        val file = File(csvFile)
        val list = file.readLines().mapNotNull {
            try {
                handlerLines(it, iconPath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


        val games = SlotMenuUtil.addCategory(list, SlotMenuUtil.microJson).map {
            it.copy(icon = it.icon.replace(" ", "").replace("®", ""),
                    touchIcon = it.touchIcon?.replace(" ", "")?.replace("®", ""))
        }

        val slotCategories = games.groupBy { it.category }.map {
            SlotCategory(gameCategory = it.key, games = it.value)
        }
        val json = jacksonObjectMapper().writeValueAsString(slotCategories)

        // 写到本地文件
        val tmpFile = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
        tmpFile.writeBytes(json.toByteArray())

        // 上传
        val url = AwsS3Util.uploadLocalFile(tmpFile, "slot/micro_gaming.json")
        println(url)

        tmpFile.delete()
    }


    fun uploadImages(local: String) {
        // 上传mg图片
        val root = File(local)
        root.listFiles().toList().parallelStream().forEach {
            val name = it.name
            val url = AwsS3Util.uploadLocalFile(it, "slot/micro_game/$name")
            println(url)
        }
    }


    fun cutImage(path: String) {
        // BTN_3Empire_ZH.png

        val file = File(path)

        val savePath = "$path/123"

        file.listFiles().filter { it.name.contains(".png") }.parallelStream().forEach {
            try {
                val list = MicroGamingCutUtil.cutImageToFile(it, savePath, 2)
                if (list.size == 1) {
                    println(it.name)
                }
            } catch (e: Exception) {

            }
        }


    }

}


fun main() {
//    val path = "/Users/cabbage/Downloads/MG__GameButtons__ALL/"
//    MicroGamingUtil.cutImage(path = path)

//     上传图片
//    val local = "/Users/cabbage/Downloads/MG__GameButtons__ALL/123"
//    MicroGamingUtil.uploadImages(local)

    // 上传json
    val csvLocal = "/Users/cabbage/Desktop/MG_Game_List_November_2019_Dashur.csv"
    val iconPath = "/Users/cabbage/Downloads/MG__GameButtons__ALL"
    MicroGamingUtil.uploadJson(csvLocal, iconPath)


//    val x = File("/Users/cabbage/Downloads/MG__GameButtons__ALL/").listFiles().firstOrNull { it.name.contains("BTN_DoubleJoker") }
//    println(x)
}
