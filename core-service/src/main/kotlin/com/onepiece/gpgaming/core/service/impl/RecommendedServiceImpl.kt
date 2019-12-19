package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.database.RecommendedValue
import com.onepiece.gpgaming.core.dao.RecommendedDao
import com.onepiece.gpgaming.core.service.RecommendedService
import org.springframework.stereotype.Service

@Service
class RecommendedServiceImpl(
        private val recommendedDao: RecommendedDao
) : RecommendedService {


    override fun all(clientId: Int): List<Recommended> {
        return recommendedDao.all(clientId)
    }

    override fun getByType(clientId: Int, type: RecommendedType): List<Recommended> {
        return recommendedDao.all(clientId).filter { it.type == type }
    }

    override fun create(co: RecommendedValue.CreateVo) {
        val state = recommendedDao.create(co)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(uo: RecommendedValue.UpdateVo) {
        val state = recommendedDao.update(uo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}