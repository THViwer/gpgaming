package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.model.token.PlaytechClientToken
import com.onepiece.gpgaming.beans.value.database.AppVersionValue
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.beans.value.internet.web.PromotionValue
import com.onepiece.gpgaming.beans.value.internet.web.SelectCountryResult
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.service.AppDownService
import com.onepiece.gpgaming.core.service.AppVersionService
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.BlogService
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.core.service.HotGameService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.SlotGameService
import com.onepiece.gpgaming.core.utils.IndexUtil
import com.onepiece.gpgaming.player.common.TransferSync
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.basic.MathUtil
import com.onepiece.gpgaming.player.controller.value.ApiValue
import com.onepiece.gpgaming.player.controller.value.BannerVo
import com.onepiece.gpgaming.player.controller.value.CompileValue
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
        private val seoService: ClientConfigService,
        private val blogService: BlogService,
        private val indexUtil: IndexUtil,
        private val appVersionService: AppVersionService
) : BasicController(), Api {

    private val log = LoggerFactory.getLogger(ApiController::class.java)

    @GetMapping
    override fun config(): IndexConfig {
        val clientId = this.getClientId()
        val url = SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile)
        return IndexConfig(url = "$url/index_${getHeaderLanguage().name.toLowerCase()}.json?${UUID.randomUUID()}")
    }

//    @GetMapping("/index")
//    override fun indexConfig(): Index {
//        val clientId = getClientIdByDomain()
//        val language = this.getHeaderLanguage()
//
//        return indexUtil.getIndexConfig(clientId = clientId, language = language)
//    }

    @GetMapping("/compile")
    override fun getConfig(): CompileValue.Config {

        val clientId = getClientId()
        val client = clientService.get(clientId)

        return CompileValue.Config(bossId = client.bossId, clientId = clientId, logo = client.logo, shortcutLogo = client.shortcutLogo)
    }

    @GetMapping("/hotGames")
    override fun hotGames(): List<HotGameVo> {
        val clientId = this.getClientId()
        val language = getHeaderLanguage()

        val type = if (getHeaderLaunch() == LaunchMethod.Wap) HotGameType.Mobile else HotGameType.Pc

        val games = hotGameService.list(clientId).filter { it.type == type }
                .let {
                    if (it.isEmpty()) hotGameService.list(1) else it
                }
                .let {
                    if (it.isEmpty()) hotGameService.list(10001) else it

                }

        if (games.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(games.first().clientId, I18nConfig.HotGame)
                .map { "${it.configId}_${it.language}" to it }
                .toMap()

        val opens = platformBindService.findClientPlatforms(clientId)
                .map { it.platform }
                .toSet()

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
    override fun indexPlatforms(): List<PlatformVo> {

        val clientId = getClientId()
        val launch = getHeaderLaunch()
        val gamePlatforms = gamePlatformService.all()

        // 平台信息
        val platformBinds = platformBindService.findClientPlatforms(clientId)
                .filter { it.status != Status.Delete }

        return platformBinds.mapNotNull {
            try {
                val gamePlatform = it.platform.getGamePlatform(gamePlatforms)

                val status = when (gamePlatform.status) {
                    Status.Normal -> it.status
                    else -> gamePlatform.status
                }

                val icon = if (launch == LaunchMethod.Wap) it.mobileIcon else it.icon
                val disableIcon = if (launch == LaunchMethod.Wap) it.mobileDisableIcon else it.disableIcon

                PlatformVo(id = it.id, name = it.name, category = it.platform.category, status = status, icon = icon,
                        launchs = gamePlatform.launchList, platform = it.platform, demo = gamePlatform.demo, disableIcon = disableIcon, originIcon = it.originIcon,
                        originIconOver = it.originIconOver, categoryDetailIcon = it.icon, platformDetailIcon = it.platformDetailIcon,
                        platformDetailIconOver = it.platformDetailIconOver, hot = it.hot, new = it.new, unclejayMobleIcon = it.unclejayMobleIcon)
                //TODO 设置图标
            } catch (e: Exception) {
                log.error("", e)
                null
            }
        }.sortedBy { it.name }
    }


    @GetMapping("/promotion/list")
    override fun promotionList(): List<PromotionVo> {
        return this.promotion()
                .distinctBy { it.id }
    }

    @GetMapping("/promotion")
    override fun promotion(): List<PromotionVo> {

        val clientId = getClientId()
        val (language, launch) = getLanguageAndLaunchFormHeader()


        val allPromotion = promotionService.all(clientId)
                .filter { it.status == Status.Normal }
                .sortedBy { it.sequence }
                .filter { it.show }
                .filter { it.category != PromotionCategory.ActivationCode } // 优惠码Code类型不显示在前台

        log.info("优惠列表：${allPromotion}")

        val promotions = arrayListOf<Promotion>()

        allPromotion.forEach { promotion ->
            // 添加默认优惠
            promotions.add(promotion)

            if (promotion.category != PromotionCategory.Other) {
                // 添加平台
                promotion.platforms.map { it.category }.toSet().map {
                    promotions.add(promotion.copy(category = it.getPromotionCategory()))
                }
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

                PromotionVo(id = promotion.id, clientId = it.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                        icon = icon, platforms = promotion.platforms, title = content.title, synopsis = content.synopsis, content = content.content,
                        status = promotion.status, createdTime = it.createdTime, precautions = content.precautions, ruleType = promotion.ruleType, rule = promotion.rule)
            }
        }

    }

    @GetMapping("/promotion/latest")
    override fun latestPromotion(): List<PromotionValue.LatestPromotionVo> {

        val clientId = getClientId()
        val promotions = promotionService.all(clientId = clientId)
                .filter { it.status == Status.Normal && it.showLatestPromotion }

        val (language, _) = getLanguageAndLaunchFormHeader()


        val contentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion)
                .filter { it.language == language }
                .map { it.configId to it }
                .toMap()

        return promotions.mapNotNull { promotion ->
            contentMap[promotion.id]
                    ?.let { content ->
                        val _c = content.getII18nContent(objectMapper = objectMapper) as I18nContent.PromotionI18n
                        _c.latestPromotionBanner?.let {
                            PromotionValue.LatestPromotionVo(promotionId = promotion.id, banner = it)
                        }
                    }

        }
    }

    @GetMapping("/config")
    override fun indexConfig(): CompileValue.AffSite {

        val bossId = getBossId()

        // 代理地址
        val affSite = webSiteService.getAffSite(clientId = bossId)?.let {
            "https://aff.${it.domain}"
        }
        return CompileValue.AffSite(path = affSite)
    }

    @GetMapping("/blog")
    override fun blogs(): List<BlogValue.BlogMVo> {

        val clientId = this.getClientId()
        val language = this.getHeaderLanguage()

        val list = blogService.normalList(clientId = clientId)

        return list.filter { it.contents.isNotEmpty() }
                .map { blog ->
                    val content = blog.contents.firstOrNull { it.language == language } ?: blog.contents.first { it.language == Language.EN }
                    BlogValue.BlogMVo(id = blog.id, title = blog.title, headImg = blog.headImg, sort = blog.sort, platform = blog.platform,
                            content = content.getII18nContent(objectMapper) as I18nContent.DefaultContentI18n, status = blog.status)
                }
    }

    @GetMapping("/i18n")
    override fun i18nConfig(@RequestParam("config") config: I18nConfig): List<I18nContent> {
        val language = getHeaderLanguage()
        val contents = i18nContentService.getConfigType(clientId = getClientId(), configType = config)

        return when (config) {
            I18nConfig.RegisterSide -> {
                val data = contents.firstOrNull { it.language == language }
                        ?: contents.firstOrNull { it.language == Language.EN }

                data?.let { listOf(it) } ?: emptyList()
            }
            else -> contents.filter { it.language == language }
        }
    }

    @GetMapping("/slots")
    override fun slots(
            @RequestParam("platform") platform: Platform
    ): List<SlotCategoryVo> {
        if (platform.category != PlatformCategory.Slot) return emptyList()

        val (language, launch) = getLanguageAndLaunchFormHeader()


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
            @RequestHeader("platform") platform: Platform
    ): StartGameResp {

        val member = current()
        val platformMember = getPlatformMember(platform, member)
        val (language, launch) = getLanguageAndLaunchFormHeader()


        transferSync.asyncTransfer(current(), platformMember)

        return when (platform) {
            Platform.PlaytechLive, Platform.PlaytechSlot -> {
                val detail = this.platformMemberDetail(platform = platform)

                val clientToken = platformBindService.find(member.clientId, platform).clientToken as PlaytechClientToken

                val lang = if (language == Language.CN) "zh-cn" else "en"
                StartGameResp(path = "-", username = detail.username, password = detail.password, params = hashMapOf(
                        "loginPath" to clientToken.loginPath,
                        "gamePath" to "${clientToken.gamePath}?language=$lang&game=7bal"
                ))
            }
            else -> {
                val gameUrl = gameApi.start(clientId = member.clientId, memberId = member.id, platformUsername = platformMember.platformUsername, platform = platform,
                        launch = launch, language = language, platformPassword = platformMember.platformPassword)
                StartGameResp(path = gameUrl, username = "-", password = "-")
            }
        }
    }

    @GetMapping("/start/demo")
    override fun startDemo(
            @RequestHeader("platform") platform: Platform
    ): StartGameResp {
        val (language, launch) = getLanguageAndLaunchFormHeader()

        val url = gameApi.startDemo(clientId = getClientId(), platform = platform, language = language, launch = launch)

        return StartGameResp(path = url)
    }

    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val (language, launch) = getLanguageAndLaunchFormHeader()

        val member = current()
        val platformMember = getPlatformMember(platform, member)

        transferSync.asyncTransfer(current(), platformMember)

        return when (platform) {
            Platform.PlaytechLive, Platform.PlaytechSlot -> {
                val detail = this.platformMemberDetail(platform = platform)
                detail.username to detail.password

                val clientToken = platformBindService.find(member.clientId, platform).clientToken as PlaytechClientToken
                val lang = if (language == Language.CN) "zh-cn" else "en"
                StartGameResp(path = "-", username = detail.username, password = detail.password, params = hashMapOf(
                        "loginPath" to clientToken.loginPath,
                        "gamePath" to "${clientToken.gamePath}?language=$lang&game=${gameId}"
                ))
            }
            else -> {
                val gameUrl = gameApi.start(clientId = member.clientId, memberId = member.id, platformUsername = platformMember.platformUsername, platform = platform,
                        gameId = gameId, language = language, launchMethod = launch, platformPassword = platformMember.platformPassword)
                StartGameResp(path = gameUrl, username = "-", password = "-")
            }
        }
    }

    @GetMapping("/start/slot/demo")
    override fun startSlotDemoGame(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val (language, launch) = getLanguageAndLaunchFormHeader()

        val gameUrl = gameApi.startSlotDemo(clientId = getClientId(), platform = platform, gameId = gameId, language = language,
                launchMethod = launch)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/down")
    override fun down(@RequestHeader("platform", required = false) platform: Platform?): List<DownloadAppVo> {
//        val gamePlatforms = gamePlatformService.all()

        val platformBinds = platformBindService.findClientPlatforms(clientId = getClientId())
        val bindMap = platformBinds.map { it.platform to it }.toMap()

        return appDownService.all()
                .filter { it.status == Status.Normal }
                .filter { platform == null || it.platform == platform }
                .map {
                    val bind = bindMap[it.platform]
                    DownloadAppVo(platform = it.platform, icon = bind?.icon ?: "", iosPath = it.iosPath, androidPath = it.androidPath)
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
            @RequestParam(value = "type") type: BannerType
    ): List<BannerVo> {
        val (language, _) = getLanguageAndLaunchFormHeader()

        val clientId = this.getClientId()

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .map { "${it.configId}:${it.language}" to it }
                .toMap()

        return bannerService.findByType(clientId = clientId, type = type).mapNotNull {
            val i18nContent = map["${it.id}:${language}"]
                    ?: map["${it.id}:${Language.EN}"]
            i18nContent?.let { x ->
                val content = i18nContent.getII18nContent(objectMapper) as I18nContent.BannerI18n

                val title = content.title ?: "this is title"
                val introduce = content.introduce ?: "this is test"
                BannerVo(id = it.id, order = it.order, icon = content.imagePath, touchIcon = content.imagePath, type = it.type,
                        link = it.link, introduce = introduce, title = title, platformCategory = it.platformCategory)
            }

        }

    }

    @GetMapping("/{category}")
    override fun categories(
            @PathVariable(value = "category") category: PlatformCategory
    ): PlatformCategoryDetail {

        val (language, launch) = getLanguageAndLaunchFormHeader()

        val clientId = this.getClientId()
        val gamePlatforms = gamePlatformService.all()

        val platforms = platformBindService.findClientPlatforms(clientId = clientId)
                .filter { it.platform.category == category }
                .map {
                    val gamePlatform = it.platform.getGamePlatform(gamePlatforms)

                    PlatformVo(id = it.id, platform = it.platform, name = it.name, category = it.platform.category,
                            status = gamePlatform.status, icon = it.icon, launchs = gamePlatform.launchList,
                            demo = gamePlatform.demo, disableIcon = it.disableIcon, originIconOver = it.originIconOver,
                            originIcon = it.originIcon, categoryDetailIcon = it.icon, platformDetailIcon = it.platformDetailIcon,
                            platformDetailIconOver = it.platformDetailIconOver, hot = it.hot, new = it.new, unclejayMobleIcon = it.unclejayMobleIcon)
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
                        introduce = content.introduce, title = content.title, platformCategory = it.platformCategory)
            }
        }

//        val games = if (category == PlatformCategory.Slot) {
//            this.slotMenu(language = language, launch = LaunchMethod.Web, platform = Platform.Pragmatic)["url"]
//        } else null

        return PlatformCategoryDetail(platforms = platforms, banners = banners)
    }

    @GetMapping("/contactUs")
    override fun contactUs(): Contacts {

        val list = contactService.list(clientId = getClientId())
                .filter { it.role == Role.Member }
                .filter { it.status == Status.Normal }

        val contacts = list.groupBy { it.type }
        val wechatContact = contacts[ContactType.Wechat]?.let { MathUtil.getRandom(it) }
        val whatContact = contacts[ContactType.Whatsapp]?.let { MathUtil.getRandom(it) }

        val facebook = list.firstOrNull { it.type == ContactType.Facebook }
        val youTuBe = list.firstOrNull { it.type == ContactType.YouTuBe }
        val instagram = list.firstOrNull { it.type == ContactType.Instagram }

        return Contacts(wechatContact = wechatContact, whatsappContact = whatContact, facebook = facebook, youtube = youTuBe,
                instagram = instagram)
    }

    @GetMapping("/seo")
    override fun seo(): ClientConfigValue.ClientConfigVo {

        val webSite = this.getWebSite()
        val clientId = when (webSite.country) {
            Country.Default -> this.getMainClient().id
            else -> webSite.clientId
        }

        val seo = seoService.get(clientId = clientId)
        return ClientConfigValue.ClientConfigVo(title = seo.title, keywords = seo.keywords, description = seo.description, liveChatId = seo.liveChatId,
                googleStatisticsId = seo.googleStatisticsId, facebookTr = seo.facebookTr, liveChatTab = seo.liveChatTab, asgContent = seo.asgContent,
                facebookShowPosition = seo.facebookShowPosition)
    }

    @GetMapping("/select/country")
    override fun selectCountry(
            @RequestParam("country") country: Country
    ): SelectCountryResult {

        val (language, launch) = getLanguageAndLaunchFormHeader()

        val bossId = getBossId()
        val clients = clientService.all().filter { it.bossId == bossId }
        val client = clients.firstOrNull { it.country == country } ?: clients.first()

        val isMobile = if (launch == LaunchMethod.Wap) "/m" else ""

        val webSites = webSiteService.all().filter { it.status == Status.Normal }.first { it.clientId == client.id }
        return SelectCountryResult(domain = "https://www.${webSites.domain}${isMobile}", language = language)
    }


    @GetMapping("/guide")
    override fun guideConfig(): ApiValue.GuideConfigVo {

        val bossId = getBossId()

        val requestURL = getRequest().requestURL.toString()
        val sites = webSiteService.all().filter { it.bossId == bossId }
                .filter { !requestURL.contains(it.domain) }
                .filter { it.status == Status.Normal }

        val clients = clientService.all().filter { it.status == Status.Normal && it.bossId == bossId }

        val defaultClient = clients.first { it.main }


        val launch = getHeaderLaunch()
        var mainPath = ""
        val countries = clients.mapNotNull { client ->
            sites.firstOrNull { it.clientId == client.id }?.let {
                val path = when (launch) {
                    LaunchMethod.Wap -> "https://www.${it.domain}/m"
                    else -> "https://www.${it.domain}"
                }
                if (it.clientId == defaultClient.id) {
                    mainPath = path
                }
                ApiValue.GuideConfigVo.CountryVo(country = it.country, path = path, main = it.clientId == defaultClient.id)
            }
        }

        return ApiValue.GuideConfigVo(logo = defaultClient.logo, countries = countries, mainPath = mainPath, shortcutLogo = defaultClient.shortcutLogo)
    }

    @GetMapping("/application/version")
    override fun checkVersion(): Map<String, AppVersionValue.AppVersionVo> {
        val mainClientId = getMainClient().id
        val list = appVersionService.getVersions(mainClientId = mainClientId)
                .map {
                    AppVersionValue.AppVersionVo(id = it.id, launch = it.launch, url = it.url, version = it.version, content = it.content, constraint = it.constraint)
                }
        val android = list.firstOrNull { it.launch == LaunchMethod.Android }
        val ios = list.firstOrNull { it.launch == LaunchMethod.Ios }

        return mapOf(
                "android" to android,
                "ios" to ios
        ).filter { it.value != null }.map { it.key to it.value!! }.toMap()
    }

    fun <T> getRandom(list: List<T>?): T? {
        return list?.let { list[Random.nextInt(list.size)] }
    }


}