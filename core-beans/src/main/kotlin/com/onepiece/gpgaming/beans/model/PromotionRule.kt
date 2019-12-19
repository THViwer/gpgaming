//package com.onepiece.treasure.beans.model
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.onepiece.treasure.beans.enums.Platform
//import com.onepiece.treasure.beans.enums.PromotionRuleCategory
//import com.onepiece.treasure.beans.enums.Status
//import java.math.BigDecimal
//import java.time.LocalDateTime
//
///**
// * 优惠活动规则
// */
//data class PromotionRule(
//
//        // id
//        val id: Int,
//
//        // 厅主Id
//        val clientId: Int,
//
//        // 平台
//        val platform: Platform,
//
//        // 优惠活动Id
//        val promotionId: Int,
//
//        // 充值送类目
//        val category: PromotionRuleCategory,
//
//        // 优惠层级Id 如果为null则是全部
//        val levelId: Int?,
//
//        // 规则
//        val ruleJson: String,
//
//        // 创建时间
//        val createdTime: LocalDateTime
//
//) {
//
//    fun <T> getPromotioRuleCondition(mapper: ObjectMapper, clz: Class<T>):  T{
//        return mapper.readValue(ruleJson, clz)
//    }
//
//}
//
//
//
//
