package com.onepiece.treasure.task

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import com.onepiece.treasure.beans.value.internet.web.SlotGame
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

        val webGames = gameApi.slotGames(clientId = 1, platform = Platform.Joker, launch = LaunchMethod.Web)
        this.upload(games = webGames, path = "slot/joker_web.json")

        val wapGames = gameApi.slotGames(clientId = 1, platform = Platform.Joker, launch = LaunchMethod.Web)
        this.upload(games = wapGames, path = "slot/joker_web.json")
    }


    private val json = "{\"vs20aladdinsorc\":\"阿拉丁和巫师\",\"vs243fortseren\":\"希腊众神\",\"vs20sbxmas\":\"甜心盛宴圣诞\",\"vs20hercpeg\":\"大力神和飞马\",\"vs10firestrike\":\"红火暴击\",\"vs20honey\":\"甜蜜蜜\",\"vs5spjoker\":\"超炫小丑\",\"vs25scarabqueen\":\"金龟子女王\",\"vs1fortunetree\":\"发发树\",\"vs20chicken\":\"小鸡大逃亡\",\"vs10vampwolf\":\"吸血鬼vs狼\",\"vs9hotroll\":\"超级辣\",\"vs7776secrets\":\"阿兹特克秘宝\",\"vs243mwarrior\":\"酷猴战士\",\"bndt\":\"龙虎斗\",\"vs5trjokers\":\"3倍小丑\",\"vs243lionsgold\":\"5金狮\",\"vs20wildpix\":\"野精灵\",\"vs20fruitsw\":\"甜入心扉\",\"vs243caishien\":\"财神运财\",\"vs40pirate\":\"夺金海贼\",\"vs20doghouse\":\"汪汪之家\",\"vs20egypttrs\":\"埃及宿命\",\"vs10fruity2\":\"额外多汁\",\"bnadvanced\":\"龙宝百家乐\",\"vs25gladiator\":\"狂野角斗士\",\"vs25goldpig\":\"招财福猪\",\"vs18mashang\":\"马上有钱\",\"vs50safariking\":\"狩猎之王\",\"vs20leprexmas\":\"小妖之歌圣诞版\",\"vs25mustang\":\"黄金野马\",\"vs5trdragons\":\"龙龙龙\",\"bca\":\"百家乐\",\"vs10egyptcls\":\"古代埃及经典版\",\"vs20vegasmagic\":\"魔力维加斯\",\"vs9chen\":\"陈师傅的财富\",\"vs25davinci\":\"达芬奇宝藏\",\"vs25peking\":\"好运北京\",\"vs20leprechaun\":\"小妖之歌\",\"vs1024butterfly\":\"玉蝴蝶\",\"vs10madame\":\"命运女士\",\"vs25asgard\":\"仙宫\",\"vs243lions\":\"5雄狮\",\"vs25champ\":\"冠军杯\",\"vs20rhino\":\"巨大犀牛\",\"vs5joker\":\"小丑珠宝\",\"vs15fairytale\":\"童话财富\",\"vs7fire88\":\"88火 \",\"vs25chilli\":\"火辣辣\",\"bjmb\":\"美式二十一点\",\"vs5aztecgems\":\"古时代宝石\",\"vs10egypt\":\"古代埃及\",\"vs25newyear\":\"幸运新年\",\"vs1tigers\":\"三只老虎\",\"vs9madmonkey\":\"猴子疯狂\",\"vs25goldrush\":\"淘金热\",\"vs20santa\":\"圣诞老人\",\"vs25pandagold\":\"熊猫财富\",\"cs5moneyroll\":\"财源滚滚\",\"vs7pigs\":\"7只小猪\",\"vs15diamond\":\"钻石罢工\",\"vs25vegas\":\"维加斯之夜\",\"vs25wildspells\":\"法力无边\",\"vs243fortune\":\"财神黄金\",\"vs50pixie\":\"精灵翅膀\",\"vs3train\":\"黄金列车\",\"vs1dragon8\":\"发发发龙\",\"vs4096jurassic\":\"侏罗纪巨兽\",\"vs20eightdragons\":\"8条龙\",\"vs25kingdoms\":\"三国\",\"vs25wolfgold\":\"野狼黄金\",\"vs25pantherqueen\":\"黑豹女王\",\"vs1024atlantis\":\"亚特兰蒂斯女王\",\"cs3irishcharms\":\"爱尔兰的魅力\",\"vs25queenofgold\":\"黄金女王\",\"cs5triple8gold\":\"富贵888\",\"bjma\":\"21点之富贵临门\",\"cs3w\":\"永恒钻石3线\",\"vs25dragonkingdom\":\"龙之国度\",\"vs50hercules\":\"宙斯之子赫拉克勒斯\",\"vs50aladdin\":\"3个精灵愿望\",\"vs30catz\":\"猫爸第二部分\",\"vs25journey\":\"西游记\",\"vs40beowulf\":\"贝奥武夫\",\"vs50chinesecharms\":\"飞龙在天\",\"vs9hockey\":\"狂野冰球赛\",\"vs25dwarves_new\":\"矮人黄金豪华版\",\"vs25romeoandjuliet\":\"罗密欧与朱丽叶\",\"vs25safari\":\"野生动物园\",\"vs9catz\":\"猫咪派对\",\"vs50kingkong\":\"无敌金刚\",\"vs20godiva\":\"女神戈帝梵\",\"vs15ktv\":\"歌厅\",\"vs243crystalcave\":\"魔幻水晶\",\"vs20hockey\":\"曲棍球联盟\",\"vs50amt\":\"阿拉丁宝藏\",\"vs13ladyofmoon\":\"月之女神\",\"vs20egypt\":\"埃及传说\",\"vs20cm\":\"极速糖果\",\"vs20cw\":\"极速糖果冬日版\",\"vs20cmv\":\"极速糖果情人版\",\"vs20cms\":\"极速糖果夏日版\",\"vs20gg\":\"幽灵财富\",\"rla\":\"轮盘\",\"kna\":\"基诺\",\"vpa\":\"杰克扑克\",\"vs25sea\":\"大堡礁\",\"vs20rome\":\"荣耀罗马\",\"vs25h\":\"水果爆破\",\"vs25dwarves\":\"矮人黄金\",\"vs13g\":\"恶魔13\",\"vs15b\":\"疯狂7s\",\"vs20bl\":\"勤劳蜜蜂\",\"vs7monkeys\":\"7只猴子\"}"
    private val pragmaticMap = objectMapper.readValue<Map<String, String>>(json)
    @Scheduled(cron="0 0 0/1 * * ? ")
//     @Scheduled(cron="0/10 * *  * * ? ")
    fun pragmaticTask() {

        listOf(LaunchMethod.Web, LaunchMethod.Wap).forEach {
            val games = gameApi.slotGames(clientId = 1, platform = Platform.Pragmatic, launch = it).map {
                val chineseGameName = pragmaticMap[it.gameId]?: it.chineseGameName
                it.copy(chineseGameName = chineseGameName)
            }
            this.upload(games = games, path = "slot/pragmatic_${it.name.toLowerCase()}.json")
        }


    }



    private fun upload(games: List<SlotGame>, path: String) {
        if (games.isEmpty()) return

        val gameCategories = games.groupBy { it.category }.map {
            SlotCategory(gameCategory = it.key, games = it.value)
        }

        val webJson = objectMapper.writeValueAsString(gameCategories)
        val file = File("${System.getProperties().getProperty("user.home")}/${UUID.randomUUID()}.json")
        file.writeBytes(webJson.toByteArray())
        AwsS3Util.uploadLocalFile(file = file, name = path)
        file.delete()
    }




}

//fun main() {
////    val file = File("/Users/cabbage/Desktop/prag.csv")
////    val map = file.readLines().map {
////        val (chineseGameName, gameId) = it.split(",")
////        gameId to chineseGameName
////    }.toMap()
//    val objectMapper = jacksonObjectMapper()
////    val json = objectMapper.writeValueAsString(map)
////    println(json)
//    val json = "{\"vs20aladdinsorc\":\"阿拉丁和巫师\",\"vs243fortseren\":\"希腊众神\",\"vs20sbxmas\":\"甜心盛宴圣诞\",\"vs20hercpeg\":\"大力神和飞马\",\"vs10firestrike\":\"红火暴击\",\"vs20honey\":\"甜蜜蜜\",\"vs5spjoker\":\"超炫小丑\",\"vs25scarabqueen\":\"金龟子女王\",\"vs1fortunetree\":\"发发树\",\"vs20chicken\":\"小鸡大逃亡\",\"vs10vampwolf\":\"吸血鬼vs狼\",\"vs9hotroll\":\"超级辣\",\"vs7776secrets\":\"阿兹特克秘宝\",\"vs243mwarrior\":\"酷猴战士\",\"bndt\":\"龙虎斗\",\"vs5trjokers\":\"3倍小丑\",\"vs243lionsgold\":\"5金狮\",\"vs20wildpix\":\"野精灵\",\"vs20fruitsw\":\"甜入心扉\",\"vs243caishien\":\"财神运财\",\"vs40pirate\":\"夺金海贼\",\"vs20doghouse\":\"汪汪之家\",\"vs20egypttrs\":\"埃及宿命\",\"vs10fruity2\":\"额外多汁\",\"bnadvanced\":\"龙宝百家乐\",\"vs25gladiator\":\"狂野角斗士\",\"vs25goldpig\":\"招财福猪\",\"vs18mashang\":\"马上有钱\",\"vs50safariking\":\"狩猎之王\",\"vs20leprexmas\":\"小妖之歌圣诞版\",\"vs25mustang\":\"黄金野马\",\"vs5trdragons\":\"龙龙龙\",\"bca\":\"百家乐\",\"vs10egyptcls\":\"古代埃及经典版\",\"vs20vegasmagic\":\"魔力维加斯\",\"vs9chen\":\"陈师傅的财富\",\"vs25davinci\":\"达芬奇宝藏\",\"vs25peking\":\"好运北京\",\"vs20leprechaun\":\"小妖之歌\",\"vs1024butterfly\":\"玉蝴蝶\",\"vs10madame\":\"命运女士\",\"vs25asgard\":\"仙宫\",\"vs243lions\":\"5雄狮\",\"vs25champ\":\"冠军杯\",\"vs20rhino\":\"巨大犀牛\",\"vs5joker\":\"小丑珠宝\",\"vs15fairytale\":\"童话财富\",\"vs7fire88\":\"88火 \",\"vs25chilli\":\"火辣辣\",\"bjmb\":\"美式二十一点\",\"vs5aztecgems\":\"古时代宝石\",\"vs10egypt\":\"古代埃及\",\"vs25newyear\":\"幸运新年\",\"vs1tigers\":\"三只老虎\",\"vs9madmonkey\":\"猴子疯狂\",\"vs25goldrush\":\"淘金热\",\"vs20santa\":\"圣诞老人\",\"vs25pandagold\":\"熊猫财富\",\"cs5moneyroll\":\"财源滚滚\",\"vs7pigs\":\"7只小猪\",\"vs15diamond\":\"钻石罢工\",\"vs25vegas\":\"维加斯之夜\",\"vs25wildspells\":\"法力无边\",\"vs243fortune\":\"财神黄金\",\"vs50pixie\":\"精灵翅膀\",\"vs3train\":\"黄金列车\",\"vs1dragon8\":\"发发发龙\",\"vs4096jurassic\":\"侏罗纪巨兽\",\"vs20eightdragons\":\"8条龙\",\"vs25kingdoms\":\"三国\",\"vs25wolfgold\":\"野狼黄金\",\"vs25pantherqueen\":\"黑豹女王\",\"vs1024atlantis\":\"亚特兰蒂斯女王\",\"cs3irishcharms\":\"爱尔兰的魅力\",\"vs25queenofgold\":\"黄金女王\",\"cs5triple8gold\":\"富贵888\",\"bjma\":\"21点之富贵临门\",\"cs3w\":\"永恒钻石3线\",\"vs25dragonkingdom\":\"龙之国度\",\"vs50hercules\":\"宙斯之子赫拉克勒斯\",\"vs50aladdin\":\"3个精灵愿望\",\"vs30catz\":\"猫爸第二部分\",\"vs25journey\":\"西游记\",\"vs40beowulf\":\"贝奥武夫\",\"vs50chinesecharms\":\"飞龙在天\",\"vs9hockey\":\"狂野冰球赛\",\"vs25dwarves_new\":\"矮人黄金豪华版\",\"vs25romeoandjuliet\":\"罗密欧与朱丽叶\",\"vs25safari\":\"野生动物园\",\"vs9catz\":\"猫咪派对\",\"vs50kingkong\":\"无敌金刚\",\"vs20godiva\":\"女神戈帝梵\",\"vs15ktv\":\"歌厅\",\"vs243crystalcave\":\"魔幻水晶\",\"vs20hockey\":\"曲棍球联盟\",\"vs50amt\":\"阿拉丁宝藏\",\"vs13ladyofmoon\":\"月之女神\",\"vs20egypt\":\"埃及传说\",\"vs20cm\":\"极速糖果\",\"vs20cw\":\"极速糖果冬日版\",\"vs20cmv\":\"极速糖果情人版\",\"vs20cms\":\"极速糖果夏日版\",\"vs20gg\":\"幽灵财富\",\"rla\":\"轮盘\",\"kna\":\"基诺\",\"vpa\":\"杰克扑克\",\"vs25sea\":\"大堡礁\",\"vs20rome\":\"荣耀罗马\",\"vs25h\":\"水果爆破\",\"vs25dwarves\":\"矮人黄金\",\"vs13g\":\"恶魔13\",\"vs15b\":\"疯狂7s\",\"vs20bl\":\"勤劳蜜蜂\",\"vs7monkeys\":\"7只猴子\"}"
//    val map = objectMapper.readValue(json, Map::class.java)
//
//    println(map)
//
//
//}