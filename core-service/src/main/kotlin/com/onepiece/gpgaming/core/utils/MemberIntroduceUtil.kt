package com.onepiece.gpgaming.core.utils

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import com.onepiece.gpgaming.core.service.PromotionService
import org.springframework.stereotype.Service

@Service
class MemberIntroduceUtil(
        private val clientConfigService: ClientConfigService,
        private val memberIntroduceService: MemberIntroduceService,
        private val promotionService: PromotionService
) {

    fun selectPromotion(clientId: Int, memberId: Int, platform: Platform) {

        val config = clientConfigService.get(clientId)
        if (!config.enableIntroduce) return

        val introduce = memberIntroduceService.get(memberId = memberId) ?: return

        val promotionId = introduce.introducePromotionId
        val promotion = promotionService.get(id = promotionId)


    }

    fun checkDepositIntroduce(memberId: Int) {

    }


}