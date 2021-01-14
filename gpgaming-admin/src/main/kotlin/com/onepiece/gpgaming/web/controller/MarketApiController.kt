package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.RegisterSource
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ClientConfig
import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.MarketDailyReportService
import com.onepiece.gpgaming.core.service.MarketService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import com.onepiece.gpgaming.web.sms.SmsService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/market")
class MarketApiController(
        private val marketService: MarketService,
        private val marketDailyReportService: MarketDailyReportService,
        private val promotionService: PromotionService,
        private val clientConfigService: ClientConfigService,
        private val memberService: MemberService,
        private val smsService: SmsService
) : BasicController(), MarketApi {

    @GetMapping
    override fun list(): List<MarketingValue.MarketVo> {

        val user = this.current()

//        val  promotions = promotionService.all(clientId = user.clientId)
        val data = marketService.find(clientId = user.clientId)

        val webSites = webSiteService.all().filter { it.clientId == user.clientId && it.status == Status.Normal }

        return data.map { market ->
//            val  promotion = promotions.first { it.id == market.promotionId }

//            val links = webSites.map { s -> "https://www.${s.domain}/market/${market.id}" }
            val affid = RegisterSource.splice(source = RegisterSource.Market, id = market.id)
            val links = webSites.map { s -> "https://www.${s.domain}/?#affid=$affid" }
            MarketingValue.MarketVo(promotionId = market.promotionId, promotionCode = market.promotionCode, messageTemplate = market.messageTemplate,
                    name = market.name, links = links, id = market.id)
        }
    }

    @PostMapping
    override fun createCo(@RequestBody co: MarketingValue.MarketingCo) {
        val user = this.current()

        val promotion = promotionService.get(co.promotionId)

        marketService.create(co.copy(clientId = user.clientId, promotionCode = promotion.code))
    }

    @PutMapping
    override fun marketUpdate(@RequestBody uo: MarketingValue.MarketingUo) {

        val promotion = promotionService.get(uo.promotionId)
        marketService.update(uo = uo.copy(promotionCode = promotion.code))
    }

    @GetMapping("/regMsgTemplate")
    override fun getRegMsgTemplate(): MarketingValue.RegisterSmsTemplateReq {
        val user = this.current()
        val config = clientConfigService.get(clientId = user.clientId)
        return MarketingValue.RegisterSmsTemplateReq(enableRegisterMessage = config.enableRegisterMessage, registerMessageTemplate = config.registerMessageTemplate,
                regainMessageTemplate = config.regainMessageTemplate)
    }

    @PutMapping("/regMsgTemplate")
    override fun regMsgTemplate(@RequestBody req: MarketingValue.RegisterSmsTemplateReq) {

        val user = this.current()
        val clientConfig = clientConfigService.get(clientId = user.clientId)
        clientConfigService.update(id = clientConfig.id, enableRegisterMessage = req.enableRegisterMessage, registerMessageTemplate = req.registerMessageTemplate,
                regainMessageTemplate = req.regainMessageTemplate)

    }

    @GetMapping("/daily/report")
    override fun marketReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = true) startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = true) endDate: LocalDate
    ): List<MarketDailyReport> {

        val user = this.current()

        val query = MarketDailyReportValue.MarketDailyReportQuery(clientId = user.clientId, startDate = startDate, endDate = endDate)
        return marketDailyReportService.list(query)
    }

    @GetMapping("/send/sms")
    override fun sendSms(
            @RequestParam("levelId", required = false) levelId: Int?,
            @RequestParam("mobiles", required = false) mobiles: String?,
            @RequestParam("content") content: String
    ) {
        val user = this.current()

        val mobileList = when {
            levelId != null -> {
                val memberQuery = MemberQuery(bossId = user.bossId, clientId = user.clientId, levelId = levelId)
                val members = memberService.query(memberQuery, 0, 5000)
                check(members.total > 500) { OnePieceExceptionCode.SMS_SMS_COUNT_MORE_THAN_500 }

                members.data.map { it.phone }
            }
            mobiles != null -> mobiles.split(",")
            else -> error(OnePieceExceptionCode.ILLEGAL_OPERATION)
        }

        smsService.send(clientId = user.id, mobiles = mobileList, message = content)
    }

    @GetMapping("/member/introduce")
    override fun getClientConfig(): ClientConfig {
        val user = this.current()
        return clientConfigService.get(clientId = user.clientId)
    }

    @PutMapping("/member/introduce")
    override fun introduceUo(@RequestBody introduceUo: ClientConfigValue.IntroduceUo) {
        val user = this.current()
        clientConfigService.update(uo = introduceUo.copy(clientId = user.clientId))
    }
}