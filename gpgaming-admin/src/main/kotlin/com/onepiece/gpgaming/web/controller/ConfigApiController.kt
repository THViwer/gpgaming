package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config")
class ConfigApiController(
        private val gamePlatformService: GamePlatformService
) : BasicController(), ConfigApi {

    @GetMapping("/enum")
    override fun allEnumType(
            @RequestHeader("language") language: Language
    ): List<EnumTypes.EnumsRespVo> {
        return EnumTypes.EnumType.values().map {
            it to this.getEnumTypes(type = it, language = language)
        }.map {
            EnumTypes.EnumsRespVo(type = it.first, data = it.second)
        }
    }

    @GetMapping("/enum/{type}")
    override fun getEnumTypes(
            @PathVariable("type") type: EnumTypes.EnumType,
            @RequestHeader("language") language: Language
    ): List<EnumTypes.EnumVo> {

        return when (type) {
            EnumTypes.EnumType.BannerEnum -> BannerType.values().map { it.name to it.name }
            EnumTypes.EnumType.BankEnum -> Bank.values().map { it.name to it.cname }
            EnumTypes.EnumType.LanguageEnum -> Language.values().map { it.name to it.name }
            EnumTypes.EnumType.PlatformEnum -> Platform.values().map { it.name to it.name }
            EnumTypes.EnumType.ContactTypeEnum -> ContactType.values().map { it.name to it.name }
            EnumTypes.EnumType.WalletEventEnum -> WalletEvent.values().map { it.name to it.name }
            EnumTypes.EnumType.PromotionCategoryEnum -> PromotionCategory.values().map { it.name to it.name }
            EnumTypes.EnumType.PlatformCategoryEnum -> PlatformCategory.values().map { it.name to it.name }
        }.map {
            EnumTypes.EnumVo(key = it.first, value = it.second)
        }
    }

    @GetMapping("/enum/bank")
    override fun getBank(): List<Bank> {
        val country = getCountryByDomain()
        return Bank.of(country = Country.Singapore)
    }
}