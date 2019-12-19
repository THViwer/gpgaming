package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.BannerType
import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Client
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.ClientDao
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ClientServiceImpl(
        private val clientDao: ClientDao,
        private val levelService: LevelService,
        private val balanceService: BalanceService,

        private val i18nContentService: I18nContentService,
        private val bannerService: BannerService,

        private val redisService: RedisService

) : ClientService {

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
        check(loginValue.password == client.password) { OnePieceExceptionCode.LOGIN_FAIL }
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
        val balanceCo = BalanceCo(clientId = id)
        balanceService.create(balanceCo)


        // 配置首页内容
        this.indexDefaultConfig(clientId = id)

    }

    private fun indexDefaultConfig(clientId: Int) {

        // 配置语言(english)
        val eContent = I18nContent.AnnouncementI18n(title = "hi", content = "hi, this is a demo")
        val i18nContentCo = I18nContentCo(clientId = clientId, language = Language.EN, configId = -1,configType = I18nConfig.Announcement, content = eContent)
        i18nContentService.create(i18nContentCo)

        // 配置语言(中文)
        val cContent = I18nContent.AnnouncementI18n(title = "你好", content = "你好，这是个例子")
        val cnI18nContentCo = i18nContentCo.copy(language = Language.CN, content = cContent)
        i18nContentService.create(cnI18nContentCo)

        // 配置语言(马来文)
        val mContent = I18nContent.AnnouncementI18n(title = "hi", content = "hi，ini adalah demo")
        val myI18nContentCo = i18nContentCo.copy(language = Language.MY, content = mContent)
        i18nContentService.create(myI18nContentCo)

        // 配置banner
        listOf(
                "https://www.bk8my.com/public/banner/banner_001_20191024101512.jpg",
                "https://www.bk8my.com/public/banner/banner_001_20191031224315.jpg",
                "https://www.bk8my.com/public/banner/banner_001_20191106023622.jpg",
                "https://www.bk8my.com/public/banner/banner_001_20191107104624.jpg",
                "https://www.bk8my.com/public/banner/banner_001_20191031224436.jpg"
        ).forEach {
            val bannerUo = BannerCo(clientId = clientId, type = BannerType.Banner, order = 1, link = null)
            val id = bannerService.create(bannerUo)

            val content = I18nContent.BannerI18n(imagePath = it)
            val i18nContent = I18nContentCo(clientId = clientId, language = Language.EN, configId = id, configType = I18nConfig.Banner, content = content)
            i18nContentService.create(i18nContent)
        }

    }

    override fun update(clientUo: ClientUo) {
        val state = clientDao.update(clientUo)
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