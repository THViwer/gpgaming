package com.onepiece.gpgaming.player.controller.value

import com.onepiece.gpgaming.beans.enums.PromotionCategory

data class PromotionCategoryVo(
        
        val promotionCategory: PromotionCategory,

        val data: List<PromotionVo>
)