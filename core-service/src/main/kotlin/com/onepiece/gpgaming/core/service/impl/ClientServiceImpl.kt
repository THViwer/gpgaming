package com.onepiece.gpgaming.core.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Client
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.database.BannerCo
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.beans.value.database.I18nContentCo
import com.onepiece.gpgaming.beans.value.database.LevelCo
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.RecommendedValue
import com.onepiece.gpgaming.core.IndexUtil
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.ClientDao
import com.onepiece.gpgaming.core.service.BalanceService
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.RecommendedService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ClientServiceImpl(
        private val clientDao: ClientDao,
        private val levelService: LevelService,
        private val balanceService: BalanceService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val i18nContentService: I18nContentService,
        private val bannerService: BannerService,
        private val redisService: RedisService,
        private val recommendedService: RecommendedService,
        private val objectMapper: ObjectMapper

) : ClientService {

    @Autowired
    @Lazy
    lateinit var indexUtil: IndexUtil

    override fun get(id: Int): Client {

        val redisKey = OnePieceRedisKeyConstant.getClient(id)

        return redisService.get(key = redisKey, clz = Client::class.java) {
            clientDao.get(id)
        }!!

    }

    override fun all(): List<Client> {
        return clientDao.all()
    }

    override fun login(loginValue: LoginValue): Client {
        val client = clientDao.findByUsername(loginValue.username)
        checkNotNull(client) { OnePieceExceptionCode.LOGIN_FAIL }
        check(bCryptPasswordEncoder.matches(loginValue.password, client.password))
        check(client.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }

        // update client
        val clientUo = ClientUo(id = client.id, ip = loginValue.ip, loginTime = LocalDateTime.now(), name = null, logo = null)
        this.update(clientUo)

        return client.copy(password = "")
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(clientCo: ClientCo) {

        // check username exist
        val hasClient = clientDao.findByUsername(clientCo.username)
        check(hasClient == null) { OnePieceExceptionCode.USERNAME_EXISTENCE }

        // insert client
        val id = clientDao.create(clientCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // create default level
        val levelCo = LevelCo(clientId = id, name = "default")
        levelService.create(levelCo)

        // create own balance
//        val balanceCo = BalanceCo(clientId = id)
//        balanceService.create(balanceCo)


        // 配置首页内容
        this.indexDefaultConfig(clientId = id)

    }

    private fun indexDefaultConfig(clientId: Int) {

        // 配置语言(english)
        also {
            val eContent = I18nContent.AnnouncementI18n(title = "hi", content = "hi, this is a demo")
            val i18nContentCo = I18nContentCo(clientId = clientId, language = Language.EN, configId = -1, configType = I18nConfig.Announcement, content = eContent)
            i18nContentService.create(i18nContentCo)

            // 配置语言(中文)
            val cContent = I18nContent.AnnouncementI18n(title = "你好", content = "你好，这是个例子")
            val cnI18nContentCo = i18nContentCo.copy(language = Language.CN, content = cContent)
            i18nContentService.create(cnI18nContentCo)

            // 配置语言(马来文)
            val mContent = I18nContent.AnnouncementI18n(title = "hi", content = "hi，ini adalah demo")
            val myI18nContentCo = i18nContentCo.copy(language = Language.MY, content = mContent)
            i18nContentService.create(myI18nContentCo)
        }

        // 配置推荐平台
        also {
            val recommendedPlatforms = Recommended.RecommendedPlatform(
                    platforms = listOf(
                            Platform.SaGaming,
                            Platform.AllBet,
                            Platform.Pragmatic,
                            Platform.Bcs,
                            Platform.CMD,
                            Platform.GGFishing,
                            Platform.Evolution
                    )
            )

            val recommendedPlatformJson = objectMapper.writeValueAsString(recommendedPlatforms)
            val recommendedPlatformCo = RecommendedValue.CreateVo(clientId = clientId, contentJson = recommendedPlatformJson, status = Status.Normal, type = RecommendedType.IndexPlatform)
            recommendedService.create(recommendedPlatformCo)
        }


        // 配置推荐视频
        also {
            val recommendedVideoCo = RecommendedValue.CreateVo(clientId = clientId, contentJson = "{}", status = Status.Normal, type = RecommendedType.IndexVideo)
            val id = recommendedService.create(recommendedVideoCo)

            val indexVideoI18n = I18nContent.IndexVideoI18n(
                    path = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/video/introduction.mp4",
                    coverPhoto = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/video/coverPhoto.png",
                    introductionImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/video/introduction.png"
            )
            val i18nContentCo = I18nContentCo(clientId = clientId, configId = id, configType = I18nConfig.IndexVideo, content = indexVideoI18n, language = Language.EN)
            i18nContentService.create(i18nContentCo)
        }


        // 体育推荐
        also {
            listOf(
                    "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/sport/s1.png",
                    "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/sport/s2.png"
            ).forEach { contentImage ->
                val sportRecommended = Recommended.RecommendedSport(platform = Platform.Bcs)
                val sportRecommendedJson = objectMapper.writeValueAsString(sportRecommended)

                val recommendedSportCo = RecommendedValue.CreateVo(clientId = clientId, contentJson = sportRecommendedJson, status = Status.Normal, type = RecommendedType.IndexSport)
                val id = recommendedService.create(recommendedSportCo)

                val indexSportI18n = I18nContent.IndexSportI18n(contentImage = contentImage)
                val i18nContentCo = I18nContentCo(clientId = clientId, configId = id, configType = I18nConfig.IndexSport, content = indexSportI18n, language = Language.EN)

                i18nContentService.create(i18nContentCo)
            }
        }

        // 真人推荐
        listOf(
                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/HighSpeedBaccarat.png",
                        platform = Platform.DreamGaming, title = "High Speed Baccarat"),

                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/Baccarat1.png",
                        platform = Platform.DreamGaming, title = "High Speed Baccarat"),

                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/Baccarat2.png",
                        platform = Platform.DreamGaming, title = "Baccarat2"),

                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/Baccarat3.png",
                        platform = Platform.DreamGaming, title = "Baccarat3"),

                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/Baccarat4.png",
                        platform = Platform.DreamGaming, title = "Baccarat4"),


                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/BidMeBaccarat1.png",
                        platform = Platform.DreamGaming, title = "BidMeBaccarat1"),

                Recommended.LiveRecommended(contentImage = "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/live/BidMeBaccarat2.png",
                        platform = Platform.DreamGaming, title = "BidMeBaccarat2")
        ).forEach {
            val json = objectMapper.writeValueAsString(it)
            val recommendedLiveCo = RecommendedValue.CreateVo(clientId = clientId, contentJson = json, status = Status.Normal, type = RecommendedType.IndexLive)
            recommendedService.create(recommendedLiveCo)
        }


        // 配置首页banner
        listOf(
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002010684576.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002013846646.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002014867116.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002015758299.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002020735253.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002021606480.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002022912586.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Banner, order = 1, link = null)
            val id = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = id, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }


        // 配置slot的banner
        listOf(
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002010684576.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002014867116.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Slot, order = 1, link = null)
            val id = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = id, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }

        // 配置Sport的banner
        listOf(
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002015758299.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002020735253.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Sport, order = 1, link = null)
            val configId = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = configId, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }

        // 配置真人的banner
        listOf(
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002021606480.jpg",
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002022912586.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Live, order = 1, link = null)
            val id = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = id, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }

        // 配置捕鱼的banner
        listOf(
                "https://s3.ap-southeast-1.amazonaws.com/awspg1/client/1/banner/2019122002013846646.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Banner, order = 1, link = null)
            val id = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = id, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }


        // 生成配置文件
        indexUtil.generatorIndexPage(clientId)
    }

    override fun update(clientUo: ClientUo) {

        val password = clientUo.password?.let {
            bCryptPasswordEncoder.encode(clientUo.password)
        }
        val state = clientDao.update(clientUo.copy(password = password))
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        val redisKey = OnePieceRedisKeyConstant.getClient(clientUo.id)
        redisService.delete(redisKey)

    }

//    override fun updateEarnestBalance(id: Int, earnestBalance: BigDecimal) {
//        val state =  this.tryUpdateEarnestBalance(index = 0, id = id, earnestBalance = earnestBalance)
//        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
//    }
//
//    private fun tryUpdateEarnestBalance(index: Int, id: Int, earnestBalance: BigDecimal): Boolean {
//
//        if (index >= 3 ) return false
//
//        val client = clientDao.get(id)
//        val state = clientDao.updateEarnestBalance(id = id, earnestBalance = earnestBalance, processId = client.processId)
//
//        if (!state) {
//            return tryUpdateEarnestBalance(index = index + 1, id = id, earnestBalance = earnestBalance)
//        }
//
//        return state
//    }

}