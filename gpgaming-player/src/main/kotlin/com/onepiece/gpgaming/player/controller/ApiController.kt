package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.core.service.AppDownService
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.games.ActiveConfig
import com.onepiece.gpgaming.player.common.TransferSync
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.BannerVo
import com.onepiece.gpgaming.player.controller.value.Contacts
import com.onepiece.gpgaming.player.controller.value.DownloadAppVo
import com.onepiece.gpgaming.player.controller.value.IndexConfig
import com.onepiece.gpgaming.player.controller.value.PlatformCategoryDetail
import com.onepiece.gpgaming.player.controller.value.PlatformCategoryPage
import com.onepiece.gpgaming.player.controller.value.PlatformMembrerDetail
import com.onepiece.gpgaming.player.controller.value.PlatformVo
import com.onepiece.gpgaming.player.controller.value.PromotionVo
import com.onepiece.gpgaming.player.controller.value.StartGameResp
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
        private val clientService: ClientService,
        private val appDownService: AppDownService,
        private val activeConfig: ActiveConfig,
        private val objectMapper: ObjectMapper
) : BasicController(), Api {

    @GetMapping
    override fun config(
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("language") language: Language
    ): IndexConfig {
        val clientId = this.getClientIdByDomain()
        val url = SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile)
        return IndexConfig(url = "$url/index_${language.name.toLowerCase()}.json?${UUID.randomUUID()}")
//        val clientId = this.getClientIdByDomain()
//
//        // 平台信息
//        val platformBinds = platformBindService.findClientPlatforms(clientId)
//        val platforms = platformBinds.map {
//
//            val status = when (it.platform.detail.status) {
//                Status.Normal -> it.status
//                else -> it.platform.detail.status
//            }
//
//            PlatformVo(id = it.id, name = it.platform.detail.name, category = it.platform.detail.category, status = status, icon = it.platform.detail.icon,
//                    launchs = it.platform.detail.launchs, platform = it.platform, demo = it.platform.detail.demo)
//        }.filter { it.platform.detail.status != Status.Delete }
//
//        // 公告
//        val contents = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Announcement)
//        val announcement = contents.firstOrNull { it.language == language }?: contents.find { it.language == Language.EN } ?: contents.first()
//        val announcementVo = AnnouncementVo(title = announcement.title, content = announcement.content, synopsis = announcement.synopsis)
//
//        // 获得首页配置
//        // banners
//        val banners = bannerService.findByType(clientId = getClientIdByDomain(), type = BannerType.Banner).map {
//            BannerVo(id = it.id, order = it.order, icon = it.icon, touchIcon = it.touchIcon, type = it.type, link = it.link)
//        }
//        // hot games
//        val size = if (launch == LaunchMethod.Wap) "10" else "20"
//        val hotGameUrl = "${SystemConstant.AWS_SLOT}/hot_sort_${size}_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
//
//        // logo
//        val client = clientService.get(clientId)
//
//        // 推荐平台
//        val recommendedPlatforms = listOf(
//                ConfigVo.RecommendedPlatform(category = PlatformCategory.Slot, platform = Platform.Kiss918,
//                        logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/918kiss.jpeg",
//                        touchLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/918kiss.jpeg"),
//
//                ConfigVo.RecommendedPlatform(category = PlatformCategory.Slot, platform = Platform.Joker,
//                        logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/joker.jpeg",
//                        touchLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/joker.jpeg"),
//
//                ConfigVo.RecommendedPlatform(category = PlatformCategory.LiveVideo, platform = Platform.AllBet,
//                        logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/allBet.jpeg",
//                        touchLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/allBet.jpeg"),
//
//                ConfigVo.RecommendedPlatform(category = PlatformCategory.Sport, platform = Platform.CMD,
//                        logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/cmd.jpeg",
//                        touchLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/cmd.jpeg"),
//
//                ConfigVo.RecommendedPlatform(category = PlatformCategory.Fishing, platform = Platform.GGFishing,
//                        logo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/fishing.jpeg",
//                        touchLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/fishing.jpeg")
//        )
//
//        // 推荐视频
//        val lastestVideo = ConfigVo.LastestVideo(path = "https://streamable.com/s/12gua/gjaita",
//                introductionImage = "https://www.bk8my.com/public/banner/videoImage_001_20191218035231_EN.png")
//
//        // 捕鱼推荐
//        val fishes = listOf(
//                ConfigVo.FishingRecommended(platform = Platform.GGFishing, contentImage = "https://www.bk8my.com/banner/ui/images/matches/upcoming-matches-1-en.png?20191218-0952",
//                        content = "ssssss"),
//                ConfigVo.FishingRecommended(platform = Platform.GGFishing, contentImage = "https://www.bk8my.com/banner/ui/images/matches/upcoming-matches-1-en.png?20191218-0952",
//                        content = "ssssss")
//        )
//
//        // 真人推荐
//        val lives = listOf(
//                ConfigVo.LiveRecommended(originLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/allBet.jpeg", platform = Platform.AllBet, title = "百家乐1",
//                        contentImage = "https://www.bk8my.com/public/new_bk8/content/images/Baccarat%201.png"),
//                ConfigVo.LiveRecommended(originLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/dreamGaming.jpeg", platform = Platform.DreamGaming, title = "百家乐2",
//                        contentImage = "https://www.bk8my.com/public/new_bk8/content/images/Baccarat%201.png"),
//                ConfigVo.LiveRecommended(originLogo = "https://s3.ap-southeast-1.amazonaws.com/awspg1/origin_logo/saGaming.jpeg", platform = Platform.SaGaming, title = "百家乐3",
//                        contentImage = "https://www.bk8my.com/public/new_bk8/content/images/Baccarat%201.png")
//        )
//
//        return ConfigVo(platforms = platforms, announcementVo = announcementVo, banners = banners, hotGameUrl = hotGameUrl, logo = client.logo,
//                recommendedPlatforms = recommendedPlatforms, lastestVideo = lastestVideo, fishes = fishes, lives = lives)

//        error("")
    }


    @GetMapping("/{gameCategory}")
    override fun categories(
            @PathVariable("category") category: PlatformCategory,
            @RequestHeader("language") language: Language
    ): PlatformCategoryPage {
        val clientId = this.getClientIdByDomain()

        val platforms = Platform.all().filter { it.detail.category == category }

        val bannerType = when (category) {
            PlatformCategory.Slot -> BannerType.Slot
            PlatformCategory.LiveVideo -> BannerType.Live
            PlatformCategory.Sport -> BannerType.Sport
            PlatformCategory.Fishing -> BannerType.Fish
            else -> error( OnePieceExceptionCode.DATA_FAIL )
        }

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        val banners = bannerService.findByType(clientId = getClientIdByDomain(), type = bannerType).map {

            val i18nContent = map["${it.id}:${language}"]
                    ?: map["${it.id}:${Language.EN}"]

            if (i18nContent == null) {
                null
            } else {
                val content = i18nContent as I18nContent.BannerI18n
                BannerVo(id = it.id, order = it.order, icon = content.imagePath , touchIcon = content.imagePath, type = it.type, link = it.link)

            }
        }.filterNotNull()

        return PlatformCategoryPage(platforms = platforms, banners = banners)
    }


    @GetMapping("/promotion")
    override fun promotion(
            @RequestHeader("language") language: Language,
            @RequestParam("promotionCategory", required = false) promotionCategory: PromotionCategory?
    ): List<PromotionVo> {

        val clientId = getClientIdByDomain()
        val promotions = promotionService.all(clientId).filter { promotionCategory == null || it.category == promotionCategory }

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        return promotions.mapNotNull { promotion ->

            val i18nContent = i18nContentMap["${promotion.id}:${language}"]
                    ?: i18nContentMap["${promotion.id}:${Language.EN}"]

            i18nContent?.let {
                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.PromotionI18n
                PromotionVo(id = it.id, clientId = it.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                        icon = content.banner, platforms = promotion.platforms, title = content.title, synopsis = content.synopsis, content = content.content,
                        status = promotion.status, createdTime = it.createdTime, precautions = content.precautions, ruleType = promotion.ruleType, rule = promotion.rule)
            }
        }

    }

    @GetMapping("/slot/menu")
    override fun slotMenu(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform): Map<String, String> {

        val url = when(platform) {
            Platform.Joker -> "${SystemConstant.AWS_SLOT}/joker_${language.name.toLowerCase()}.json"
            Platform.MicroGaming -> "${SystemConstant.AWS_SLOT}/micro_gaming_${language.name.toLowerCase()}.json"
            Platform.Pragmatic -> "${SystemConstant.AWS_SLOT}/pragmatic_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
            Platform.SpadeGaming -> "${SystemConstant.AWS_SLOT}/spade_game_${language.name.toLowerCase()}.json"
            Platform.TTG -> "${SystemConstant.AWS_SLOT}/ttg_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
            Platform.PNG -> "${SystemConstant.AWS_SLOT}/png_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json"
            Platform.GamePlay -> "${SystemConstant.AWS_SLOT}/gameplay_${language.name.toLowerCase()}.json"
            Platform.SimplePlay -> "${SystemConstant.AWS_SLOT}/simple_play_${language.name.toLowerCase()}.json"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
        return mapOf(
                "url" to "$url?${UUID.randomUUID()}"
        )
    }

    @GetMapping("/start")
    override fun start(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch") launch: LaunchMethod
    ): StartGameResp {
        val member = current()
        val platformMember = getPlatformMember(platform)

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                launch = launch, language = language, platformPassword = platformMember.platformPassword)

        transferSync.asyncTransfer(current(), platformMember)

        return StartGameResp(path = gameUrl)
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

        val platformMember = getPlatformMember(platform)
        val member = current()

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                gameId = gameId, language = language, launchMethod = launch)

        transferSync.asyncTransfer(current(), platformMember)

        return StartGameResp(path = gameUrl)

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
        return appDownService.all().filter { it.status == Status.Normal }.map {
            DownloadAppVo(platform = it.platform, icon = it.platform.detail.icon, iosPath = it.iosPath, androidPath = it.androidPath)
        }
    }

    @GetMapping("/platform/member")
    override fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail {
        return getPlatformMember(platform).let {
            PlatformMembrerDetail(username = it.platformUsername, password = it.platformPassword)
        }
    }

    @GetMapping("/{category}")
    override fun categorys(
            @RequestHeader("language") language: Language,
            @PathVariable("category") category: PlatformCategory
    ): PlatformCategoryDetail {

        val clientId = this.getClientIdByDomain()

        val platforms = platformBindService.findClientPlatforms(clientId = getClientIdByDomain())
                .filter { it.platform.detail.category == category }
                .map {
                    PlatformVo(id = it.id, platform = it.platform, name = it.platform.detail.name, category = it.platform.detail.category,
                            status = it.platform.detail.status, icon = it.platform.detail.icon, launchs = it.platform.detail.launchs,
                            demo = it.platform.detail.demo)
                }

        val type = when (category) {
            PlatformCategory.Fishing -> BannerType.Fish
            PlatformCategory.Slot -> BannerType.Slot
            PlatformCategory.LiveVideo -> BannerType.Live
            PlatformCategory.Sport -> BannerType.Sport
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        val banners = bannerService.findByType(clientId = getClientIdByDomain(), type = type).map {
            val i18nContent = map["${it.id}:${language}"]
                    ?: map["${it.id}:${Language.EN}"]
            if (i18nContent == null) {
                null
            } else {
                val content = i18nContent as I18nContent.BannerI18n
                BannerVo(id = it.id, order = it.order, icon = content.imagePath , touchIcon = content.imagePath, type = it.type, link = it.link)
            }
        }.filterNotNull()

        val games = if (category == PlatformCategory.Slot) {
            this.slotMenu(language = language, launch = LaunchMethod.Web, platform = Platform.Pragmatic)["url"]
        } else null

        return PlatformCategoryDetail(platforms = platforms, banners = banners, url = games )
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