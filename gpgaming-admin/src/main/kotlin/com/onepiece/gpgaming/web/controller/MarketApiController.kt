package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.MarketDailyReport
import com.onepiece.gpgaming.beans.value.database.MarketDailyReportValue
import com.onepiece.gpgaming.beans.value.database.MarketingValue
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.MarketDailyReportService
import com.onepiece.gpgaming.core.service.MarketService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.web.controller.basic.BasicController
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
        private val clientConfigService: ClientConfigService
) : BasicController(), MarketApi {

    @GetMapping
    override fun list(): List<MarketingValue.MarketVo> {

        val user = this.current()

//        val  promotions = promotionService.all(clientId = user.clientId)
        val data = marketService.find(clientId = user.clientId)

        val webSites = webSiteService.all().filter { it.clientId == user.clientId && it.status == Status.Normal }

        return data.map { market ->
//            val  promotion = promotions.first { it.id == market.promotionId }

            val links = webSites.map { s -> "https://www.${s.domain}/market/${market.id}" }
            MarketingValue.MarketVo(promotionId = market.promotionId, promotionCode = market.promotionCode, messageTemplate = market.messageTemplate,
                    name = market.name, links = links)
        }
    }

    @PostMapping
    override fun createCo(@RequestBody co: MarketingValue.MarketingCo) {
        val user = this.current()
        marketService.create(co.copy(clientId = user.clientId))
    }

    @PutMapping
    override fun marketUpdate(@RequestBody uo: MarketingValue.MarketingUo) {
        marketService.update(uo = uo)
    }

    @GetMapping("/regMsgTemplate")
    override fun getRegMsgTemplate(): MarketingValue.RegisterSmsTemplateReq {
        val user = this.current()
        val config = clientConfigService.get(clientId = user.clientId)
        return MarketingValue.RegisterSmsTemplateReq(enableRegisterMessage = config.enableRegisterMessage, registerMessageTemplate = config.registerMessageTemplate)
    }

    @PutMapping("/regMsgTemplate")
    override fun regMsgTemplate(@RequestBody req: MarketingValue.RegisterSmsTemplateReq) {

        val user  = this.current()
        val clientConfig  = clientConfigService.get(clientId = user.clientId)
        clientConfigService.update(id = clientConfig.id, enableRegisterMessage = req.enableRegisterMessage, registerMessageTemplate = req.registerMessageTemplate)

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
}