package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Language
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

    @ApiOperation(tags = ["config"], value = "获得银行列表")
    fun getBank(): List<Bank>

}

sealed class EnumTypes {
    enum class EnumType {

        // banner
        BannerEnum,

        // 银行列表
        BankEnum,

        // 支持语言
        LanguageEnum,

        // 平台列表
        PlatformEnum,

        // 联系我们类型
        ContactTypeEnum,

        // 钱包事件
        WalletEventEnum,

        // 优惠类型
        PromotionCategoryEnum,

        // 平台类别
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

