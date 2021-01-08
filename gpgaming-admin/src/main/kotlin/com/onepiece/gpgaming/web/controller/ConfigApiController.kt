package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.FileCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.email.EmailSMTPService
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.utils.AwsS3Util
import com.onepiece.gpgaming.web.controller.basic.BasicController
import com.onepiece.gpgaming.web.controller.value.EmailReq
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ConfigApiController(
        private val gamePlatformService: GamePlatformService,
        private val activeConfig: ActiveConfig,
        private val memberService: MemberService,
        private val emailSMTPService: EmailSMTPService
) : BasicController(), ConfigApi {

    @GetMapping("/config/enum")
    override fun allEnumType(
            @RequestHeader("language") language: Language
    ): List<EnumTypes.EnumsRespVo> {
        return EnumTypes.EnumType.values().map {
            it to this.getEnumTypes(type = it, language = language)
        }.map {
            EnumTypes.EnumsRespVo(type = it.first, data = it.second)
        }
    }

    @GetMapping("/config/enum/{type}")
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

    @GetMapping("/config/enum/bank")
    override fun getBank(): List<Bank> {
        val country = getCountryByDomain()
        return Bank.of(country = country)
    }

    @PostMapping("/file/upload")
    override fun uploadProof(
            @RequestParam("category") category: FileCategory,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {
        val clientId = getClientId()

//        val path = category.path
//                .let {
//                    SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile, defaultPath = it)
//
//                }
        val url = AwsS3Util.clientUpload(file = file, clientId = clientId, path = category.path, profile = activeConfig.profile)
        return mapOf(
                "path" to url
        )
    }

    @PostMapping("/email/send")
    override fun sendEmail(@RequestBody req: EmailReq) {

        if (req.memberIds.isEmpty()) return
        val members = memberService.findByIds(ids = req.memberIds)
        if (members.isEmpty()) return

        val emails = members.mapNotNull { it.email }
        if (emails.isEmpty()) return

        emailSMTPService.sends(emails = emails.joinToString(separator = ","), smtp_server = req.smtp_server, auth_username = req.auth_username, auth_password = req.auth_username,
                content = req.content)
    }

}