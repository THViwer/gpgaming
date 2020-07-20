package com.onepiece.gpgaming.core.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.model.PromotionRules
import com.onepiece.gpgaming.beans.value.database.PromotionCo
import com.onepiece.gpgaming.beans.value.database.PromotionUo
import com.onepiece.gpgaming.beans.value.internet.web.PromotionCoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionUoReq
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.PromotionDao
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.utils.RedisService
import com.onepiece.gpgaming.utils.StringUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class PromotionServiceImpl(
        private val promotionDao: PromotionDao,
        private val redisService: RedisService,
        private val objectMapper: ObjectMapper
) : PromotionService {

    override fun all(clientId: Int): List<Promotion> {

        val redisKey = OnePieceRedisKeyConstant.promotions(clientId)
        return redisService.getList(redisKey, Promotion::class.java) {
            promotionDao.all(clientId)
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(clientId: Int, promotionCoReq: PromotionCoReq) {

        if (promotionCoReq.code != null) {
            val has = this.all(clientId = clientId).firstOrNull { it.code == promotionCoReq.code }
            check(has == null) { OnePieceExceptionCode.PROMOTION_CODE_EXIST }
        }
//        val code = this.getCode(clientId = clientId)
        // 创建优惠记录
        val promotionCo = PromotionCo(clientId = clientId, category = promotionCoReq.category, stopTime = promotionCoReq.stopTime, top = promotionCoReq.top,
                levelId = promotionCoReq.levelId, ruleType = promotionCoReq.promotionRuleVo.ruleType, periodMaxPromotion = promotionCoReq.periodMaxPromotion,
                ruleJson = promotionCoReq.promotionRuleVo.ruleJson, platforms = promotionCoReq.platforms, period = promotionCoReq.period,
                sequence = promotionCoReq.sequence, show = promotionCoReq.show, code = promotionCoReq.code?: "")
        val promotionId = promotionDao.create(promotionCo)
        check(promotionId > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotionCo.clientId))
    }

    fun getCode(clientId: Int): String {

        val code = StringUtil.generateNonce(6).toUpperCase()

        this.all(clientId = clientId).firstOrNull { it.code == code } ?: return code

        return this.getCode(clientId = clientId)
    }


    override fun update(clientId: Int, promotionUoReq: PromotionUoReq) {

        val promotion = promotionDao.get(promotionUoReq.id)

        // 更新优惠记录
        val promotionUo = PromotionUo(id = promotionUoReq.id, category = promotionUoReq.category, stopTime = promotionUoReq.stopTime,
                top = promotionUoReq.top, status = promotionUoReq.status, levelId = promotionUoReq.levelId, periodMaxPromotion = promotionUoReq.periodMaxPromotion,
                ruleJson = promotionUoReq.promotionRuleVo?.ruleJson, platforms = promotionUoReq.platforms, period = promotionUoReq.period, sequence = promotionUoReq.sequence,
                show = promotionUoReq.show)
        val state = promotionDao.update(promotionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 更新优惠活动
//        if (promotionUoReq.ruleJson != null) {
//            val promotionRuleUo = PromotionRuleUo(promotionId = promotionUoReq.id , ruleJson = promotionUoReq.ruleJson)
//            promotionRoleDao.update(promotionRuleUo)
//        }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotion.clientId))
    }

    override fun get(id: Int): Promotion {

        return if (id == -100) {

            val now = LocalDateTime.now()

            val rule = PromotionRules.WithdrawRule(minAmount = BigDecimal.ZERO, maxAmount = BigDecimal.valueOf(99999999), ignoreTransferOutAmount = BigDecimal.valueOf(5),
                    maxPromotionAmount = BigDecimal.ONE, transferMultiplied = BigDecimal.valueOf(2), promotionProportion = BigDecimal.ZERO)
            val ruleJson = objectMapper.writeValueAsString(rule)

            return Promotion(id = -100, clientId = 0, category = PromotionCategory.Special, platforms = listOf(Platform.Kiss918, Platform.Pussy888, Platform.Mega),
                    stopTime = null, ruleType = PromotionRuleType.Withdraw, levelId = emptyList(), period = PromotionPeriod.Daily, periodMaxPromotion = BigDecimal(99999999),
                    ruleJson = ruleJson, top = true, status = Status.Normal, createdTime = now, updatedTime = now, sequence = 100, show = true, code = "")
        } else {
            promotionDao.get(id)
        }
    }

    override fun find(clientId: Int, platform: Platform): List<Promotion> {
        return this.all(clientId).filter { it.status == Status.Normal }.filter { it.platforms.contains(platform) }
    }

}