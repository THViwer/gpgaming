package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Language
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@Api(tags = ["config"], description = " ")
interface ConfigApi {

    @ApiOperation(tags = ["config"], value = "enum类型获取")
    fun allEnumType(@RequestHeader("language") language: Language): List<EnumTypes.EnumsRespVo>

    @ApiOperation(tags = ["config"], value = "enum类型获取")
    fun getEnumTypes(
            @PathVariable("type") type: EnumTypes.EnumType,
            @RequestHeader("language") language: Language
    ): List<EnumTypes.EnumVo>

}

sealed class EnumTypes {
    enum class EnumType {

        BannerEnum,

        BankEnum,

        LanguageEnum,

        PlatformEnum,

        ContactTypeEnum,

        WalletEventEnum,

        PromotionCategoryEnum,

        PlatformCategoryEnum

    }

    data class EnumsRespVo(
            val type: EnumType,

            val data: List<EnumVo>

    )

    data class EnumVo(
            val key: String,

            val value: String
    )

}

