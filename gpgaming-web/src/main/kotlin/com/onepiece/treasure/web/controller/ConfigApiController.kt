package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config")
class ConfigApiController : ConfigApi {

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
            EnumTypes.EnumType.PlatformEnum -> Platform.values().map { it.name to it.detail.name }
            EnumTypes.EnumType.ContactTypeEnum -> ContactType.values().map { it.name to it.name }
            EnumTypes.EnumType.WalletEventEnum -> WalletEvent.values().map { it.name to it.name }
            EnumTypes.EnumType.PromotionCategoryEnum -> PromotionCategory.values().map { it.name to it.name }
            EnumTypes.EnumType.PlatformCategoryEnum -> PlatformCategory.values().map { it.name to it.name }
        }.map {
            EnumTypes.EnumVo(key = it.first, value = it.second)
        }



    }
}