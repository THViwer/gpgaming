package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.service.AppDownService
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.HotGameService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.SlotGameService
import com.onepiece.gpgaming.player.common.TransferSync
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.BannerVo
import com.onepiece.gpgaming.player.controller.value.Contacts
import com.onepiece.gpgaming.player.controller.value.DownloadAppVo
import com.onepiece.gpgaming.player.controller.value.HotGameVo
import com.onepiece.gpgaming.player.controller.value.IndexConfig
import com.onepiece.gpgaming.player.controller.value.PlatformCategoryDetail
import com.onepiece.gpgaming.player.controller.value.PlatformMembrerDetail
import com.onepiece.gpgaming.player.controller.value.PlatformVo
import com.onepiece.gpgaming.player.controller.value.PromotionVo
import com.onepiece.gpgaming.player.controller.value.SlotCategoryVo
import com.onepiece.gpgaming.player.controller.value.SlotGameVo
import com.onepiece.gpgaming.player.controller.value.StartGameResp
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.random.Random

@Suppress("CAST_NEVER_SUCCEEDS")
@RestController
@RequestMapping("/api")
open class ApiController(
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService,
        private val bannerService: BannerService,
        private val contactService: ContactService,
        private val transferSync: TransferSync,
        private val slotGameService: SlotGameService,
        private val appDownService: AppDownService,
        private val activeConfig: ActiveConfig,
        private val objectMapper: ObjectMapper,
        private val hotGameService: HotGameService,
        private val gamePlatformService: GamePlatformService
) : BasicController(), Api {

    private val log = LoggerFactory.getLogger(ApiController::class.java)

    @GetMapping
    override fun config(
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("language") language: Language
    ): IndexConfig {
        val clientId = this.getClientIdByDomain()
        val url = SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile)
        return IndexConfig(url = "$url/index_${language.name.toLowerCase()}.json?${UUID.randomUUID()}")
    }

    @GetMapping("/hotGames")
    override fun hotGames(
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("language") language: Language
    ): List<HotGameVo> {
        val clientId = this.getClientIdByDomain()

        val type = if (launch == LaunchMethod.Wap) HotGameType.Mobile else HotGameType.Pc

        val games = hotGameService.list(clientId).filter { it.type == type }
                .let {
                    if (it.isEmpty()) hotGameService.list(1) else it
                }

        if (games.isEmpty()) return emptyList()

//        log.info("step 1 ${games}")

        val i18nContentMap = i18nContentService.getConfigType(games.first().clientId, I18nConfig.HotGame)
//                .filter { it.language == language }
                .map { "${it.configId}_${it.language}" to it }
                .toMap()

//        if (i18nContentMap.isEmpty()) return emptyList()
//        log.info("step 2 $i18nContentMap")


        val opens = platformBindService.findClientPlatforms(clientId)
//                .filter { it.platform.category == PlatformCategory.Slot }
                .map { it.platform }
                .toSet()

//        log.info("step 3 $opens")


        if (opens.isEmpty()) return emptyList()

        return games.mapNotNull {
            when {
                i18nContentMap["${it.id}_${language}"] != null -> {
                    val content = i18nContentMap["${it.id}_${language}"]!!
                    val hotGameContent = content.getII18nContent(objectMapper) as I18nContent.HotGameI18n
                    HotGameVo(name = hotGameContent.name, introduce = hotGameContent.introduce, gameId = it.gameId, img1 = hotGameContent.img1, img2 = hotGameContent.img2,
                            img3 = hotGameContent.img3, platform = it.platform, logo = it.platform.hotGameLogo)
                }
                i18nContentMap["${it.id}_${Language.EN}"] != null -> {
                    val content = i18nContentMap["${it.id}_${Language.EN}"]!!
                    val hotGameContent = content.getII18nContent(objectMapper) as I18nContent.HotGameI18n
                    HotGameVo(name = hotGameContent.name, introduce = hotGameContent.introduce, gameId = it.gameId, img1 = hotGameContent.img1, img2 = hotGameContent.img2,
                            img3 = hotGameContent.img3, platform = it.platform, logo = it.platform.hotGameLogo)
                }
                else -> null
            }

        }.filter { opens.contains(it.platform) }

    }

    @GetMapping("/index/platforms")
    override fun indexPlatforms(
            @RequestHeader("launch", defaultValue = "Wap") launch: LaunchMethod
    ): List<PlatformVo> {

        val clientId = getClientIdByDomain()
        val gamePlatforms = gamePlatformService.all()

        // 平台信息
        val platformBinds = platformBindService.findClientPlatforms(clientId)

        return platformBinds.map {
            val gamePlatform = it.platform.getGamePlatform(gamePlatforms)

            val status = when (gamePlatform.status) {
                Status.Normal -> it.status
                else -> gamePlatform.status
            }

            val icon = if (launch == LaunchMethod.Wap) gamePlatform.mobileIcon else gamePlatform.icon
            val disableIcon = if (launch == LaunchMethod.Wap) gamePlatform.mobileDisableIcon else gamePlatform.disableIcon

            PlatformVo(id = it.id, name = gamePlatform.name, category = it.platform.category, status = status, icon = icon,
                    launchs = gamePlatform.launchList, platform = it.platform, demo = gamePlatform.demo, disableIcon = disableIcon, originIcon = gamePlatform.originIcon,
                    originIconOver = gamePlatform.originIconOver, categoryDetailIcon = gamePlatform.icon, platformDetailIcon = gamePlatform.platformDetailIcon,
                    platformDetailIconOver = gamePlatform.platformDetailIconOver)
            //TODO 设置图标
        }.filter { it.status != Status.Delete }.sortedBy { it.name }
    }

    @GetMapping("/promotion")
    override fun promotion(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod
    ): List<PromotionVo> {

        val clientId = getClientIdByDomain()

        val allPromotion = promotionService.all(clientId).filter { it.status == Status.Normal }

        val promotions = arrayListOf<Promotion>()

        allPromotion.forEach { promotion ->
            // 添加默认优惠
            promotions.add(promotion)
            // 添加平台
            promotion.platforms.map { it.category }.toSet().map {
                promotions.add(promotion.copy(category = it.getPromotionCategory()))
            }
        }

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        return promotions.mapNotNull { promotion ->

            val i18nContent = i18nContentMap["${promotion.id}:${language}"]
                    ?: i18nContentMap["${promotion.id}:${Language.EN}"]


            i18nContent?.let {
                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.PromotionI18n

                val icon = if (launch == LaunchMethod.Wap) {
                    content.mobileBanner
                } else {
                    content.banner
                }

                PromotionVo(id = it.id, clientId = it.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                        icon = icon, platforms = promotion.platforms, title = content.title, synopsis = content.synopsis, content = content.content,
                        status = promotion.status, createdTime = it.createdTime, precautions = content.precautions, ruleType = promotion.ruleType, rule = promotion.rule)
            }
        }

    }

//    @GetMapping("/slot/menu")
//    override fun slotMenu(
//            @RequestHeader("language") language: Language,
//            @RequestHeader("launch") launch: LaunchMethod,
//            @RequestParam("platform") platform: Platform): Map<String, String> {
//
//        val url = when(platform) {
//            Platform.Joker -> "${SystemConstant.AWS_SLOT}/joker_${language.name.toLowerCase()}.json"
//            Platform.MicroGaming -> "${SystemConstant.AWS_SLOT}/micro_gaming_${language.name.toLowerCase()}.json"
//            Platform.Pragmatic -> "${SystemConstant.AWS_SLOT}/pragmatic_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//            Platform.SpadeGaming -> "${SystemConstant.AWS_SLOT}/spade_game_${language.name.toLowerCase()}.json"
//            Platform.TTG -> "${SystemConstant.AWS_SLOT}/ttg_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//            Platform.PNG -> "${SystemConstant.AWS_SLOT}/png_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//            Platform.GamePlay -> "${SystemConstant.AWS_SLOT}/gameplay_${language.name.toLowerCase()}.json"
//            Platform.SimplePlay -> "${SystemConstant.AWS_SLOT}/simple_play_${language.name.toLowerCase()}.json"
//            Platform.PlaytechSlot -> "${SystemConstant.AWS_SLOT}/playtech_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//            Platform.AsiaGamingSlot -> "${SystemConstant.AWS_SLOT}/asia_gaming_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//            else -> error(OnePieceExceptionCode.DATA_FAIL)
//        }
//        return mapOf(
//                "url" to "$url?${UUID.randomUUID()}"
//        )
//    }



    @GetMapping("/slots")
    override fun slots(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform
    ): List<SlotCategoryVo> {
        if (platform.category != PlatformCategory.Slot) return emptyList()

        val list = slotGameService.findByPlatform(platform)
        if (list.isEmpty()) return emptyList()

        val games = list.filter {
            it.launchs.toString().contains(launch.toString())
        }.map { slot ->
            val gameName = if (language == Language.CN) slot.cname else slot.ename
            val icon = if (language == Language.CN) slot.clogo else slot.elogo

            SlotGameVo(platform = slot.platform, gameId = slot.gameId, category = slot.category, gameName = gameName, icon = icon,
                    touchIcon = null, hot = slot.hot, new = slot.new, status = slot.status)
        }

        val hots = games.filter { it.hot }.let { GameCategory.Hot to it }
        val news = games.filter { it.new }.let { GameCategory.New to it }

        return games.groupBy { it.category }.plus(hots).plus(news)
                .map {
                    SlotCategoryVo(gameCategory = it.key, games = it.value)
                }

    }

    @GetMapping("/start")
    override fun start(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch") launch: LaunchMethod
    ): StartGameResp {

        val member = current()
        val platformMember = getPlatformMember(platform, member)

        transferSync.asyncTransfer(current(), platformMember)

        return when (platform) {
            Platform.PlaytechLive, Platform.PlaytechSlot -> {
                val detail = this.platformMemberDetail(platform = platform)
                StartGameResp(path = "-", username = detail.username, password = detail.password)
            }
            else -> {
                val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                        launch = launch, language = language, platformPassword = platformMember.platformPassword)
                StartGameResp(path = gameUrl, username = "-", password = "-")
            }
        }
    }

    @GetMapping("/start/demo")
    override fun startDemo(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch") launch: LaunchMethod
    ): StartGameResp {
        val url = gameApi.startDemo(clientId = getClientIdByDomain(), platform = platform, language = language, launch = launch)

        return StartGameResp(path = url)
    }

    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val member = current()
        val platformMember = getPlatformMember(platform, member)

        transferSync.asyncTransfer(current(), platformMember)

        return when (platform) {
            Platform.PlaytechLive, Platform.PlaytechSlot -> {
                val detail = this.platformMemberDetail(platform = platform)
                detail.username to detail.password

                val clientToken = platformBindService.find(member.clientId, platform).clientToken as PlaytechClientToken
                StartGameResp(path = "-", username = detail.username, password = detail.password, params = hashMapOf("serverName" to clientToken.serverName))
            }
            else -> {
                val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                        gameId = gameId, language = language, launchMethod = launch, platformPassword = platformMember.platformPassword)
                StartGameResp(path = gameUrl, username = "-", password = "-")
            }
        }
    }

    @GetMapping("/start/slot/demo")
    override fun startSlotDemoGame(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val gameUrl = gameApi.startSlotDemo(clientId = getClientIdByDomain(), platform = platform, gameId = gameId, language = language,
                launchMethod = launch)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/down")
    override fun down(@RequestHeader("platform", required = false) platform: Platform?): List<DownloadAppVo> {
        val gamePlatforms = gamePlatformService.all()

        return appDownService.all()
                .filter { it.status == Status.Normal }
                .filter { platform == null || it.platform == platform }
                .map {
            val gamePlatform = it.platform.getGamePlatform(gamePlatforms)
            DownloadAppVo(platform = it.platform, icon = gamePlatform.icon, iosPath = it.iosPath, androidPath = it.androidPath)
        }
    }

    @GetMapping("/platform/member")
    override fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail {

        val current = current()

        return getPlatformMember(platform, current).let {
            val username = when (platform) {
                Platform.PlaytechSlot, Platform.PlaytechLive -> {
                    val bind = platformBindService.findClientPlatforms(clientId = current.clientId).first { it.platform == platform }
                    val clientToken = bind.clientToken as PlaytechClientToken
                    "${clientToken.prefix}_${it.platformUsername}".toUpperCase()
                }
                else -> it.platformUsername
            }
            PlatformMembrerDetail(username = username, password = it.platformPassword)
        }
    }

    //    @GetMapping("/pc/{category}")
//    override fun categories(
//            @PathVariable("category") category: PlatformCategory,
//            @RequestHeader("language") language: Language
//    ): PlatformCategoryPage {
//        val clientId = this.getClientIdByDomain()
//
//        val gamePlatforms = gamePlatformService.all()
//
//        val platforms = Platform.all().filter { it.category == category }
//
//        val bannerType = when (category) {
//            PlatformCategory.Slot -> BannerType.Slot
//            PlatformCategory.LiveVideo -> BannerType.Live
//            PlatformCategory.Sport -> BannerType.Sport
//            PlatformCategory.Fishing -> BannerType.Fish
//            else -> error( OnePieceExceptionCode.DATA_FAIL )
//        }
//
//        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
//                .map { "${it.configId}:${it.language}" to it }
//                .toMap()
//
//        val banners = bannerService.findByType(clientId = getClientIdByDomain(), type = bannerType).map {
//
//            val i18nContent = map["${it.id}:${language}"]
//                    ?: map["${it.id}:${Language.EN}"]
//
//            if (i18nContent == null) {
//                null
//            } else {
//                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.BannerI18n
//                BannerVo(id = it.id, order = it.order, icon = content.imagePath , touchIcon = content.imagePath, type = it.type, link = it.link)
//
//            }
//        }.filterNotNull()
//
//        return PlatformCategoryPage(platforms = platforms, banners = banners)
//    }

    @GetMapping("/banner")
    override fun banners(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam(value =  "type") type: BannerType
    ): List<BannerVo> {

        val clientId = this.getClientIdByDomain()

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        return bannerService.findByType(clientId = getClientIdByDomain(), type = type).mapNotNull {
            val i18nContent = map["${it.id}:${language}"]
                    ?: map["${it.id}:${Language.EN}"]
            i18nContent?.let { x ->
                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.BannerI18n

                val title = content.title ?: "this is title"
                val introduce = content.introduce ?: "this is test"
                BannerVo(id = it.id, order = it.order, icon = content.imagePath, touchIcon = content.imagePath, type = it.type,
                        link = it.link, introduce = introduce, title = title)
            }

        }

    }

    @GetMapping("/{category}")
    override fun categorys(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @PathVariable(value =  "category") category: PlatformCategory
    ): PlatformCategoryDetail {

        val clientId = this.getClientIdByDomain()
        val gamePlatforms = gamePlatformService.all()

        val platforms = platformBindService.findClientPlatforms(clientId = getClientIdByDomain())
                .filter { it.platform.category == category }
                .map {
                    val gamePlatform = it.platform.getGamePlatform(gamePlatforms)

                    PlatformVo(id = it.id, platform = it.platform, name = gamePlatform.name, category = it.platform.category,
                            status = gamePlatform.status, icon = gamePlatform.icon, launchs = gamePlatform.launchList,
                            demo = gamePlatform.demo, disableIcon = gamePlatform.disableIcon, originIconOver = gamePlatform.originIconOver,
                            originIcon = gamePlatform.originIcon, categoryDetailIcon = gamePlatform.icon, platformDetailIcon = gamePlatform.platformDetailIcon,
                            platformDetailIconOver = gamePlatform.platformDetailIconOver)
                    //TODO 配置图标
                }

        val type = when (category) {
            PlatformCategory.Fishing -> BannerType.Fish
            PlatformCategory.Slot -> BannerType.Slot
            PlatformCategory.LiveVideo -> BannerType.Live
            PlatformCategory.Sport -> BannerType.Sport
            else -> {
                if (launch == LaunchMethod.Wap) BannerType.MobileBanner else BannerType.Banner
            }
        }

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        val banners = bannerService.findByType(clientId = clientId, type = type).mapNotNull {
            val i18nContent = map["${it.id}:${language}"]
                    ?: map["${it.id}:${Language.EN}"]
            if (i18nContent == null) {
                null
            } else {
                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.BannerI18n
                BannerVo(id = it.id, order = it.order, icon = content.imagePath, touchIcon = content.imagePath, type = it.type, link = it.link,
                        introduce = content.introduce, title = content.title)
            }
        }

//        val games = if (category == PlatformCategory.Slot) {
//            this.slotMenu(language = language, launch = LaunchMethod.Web, platform = Platform.Pragmatic)["url"]
//        } else null

        return PlatformCategoryDetail(platforms = platforms, banners = banners )
    }

    @GetMapping("/contactUs")
    override fun contactUs(): Contacts {
        val contacts = contactService.list(clientId = getClientIdByDomain()).groupBy { it.type }
        val wechatContact = contacts[ContactType.Wechat]?.let { getRandom(it) }
        val whatContact = contacts[ContactType.Whatsapp]?.let { getRandom(it) }
        return Contacts(wechatContact = wechatContact, whatsappContact = whatContact)
    }


    fun <T> getRandom(list: List<T>?) : T? {
        return list?.let { list[Random.nextInt(list.size)] }
    }

}