package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.MemberAnalysisSort
import com.onepiece.gpgaming.beans.value.database.MemberValue
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.utils.Query
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AnalysisDaoImpl(
        private val jdbcTemplate: JdbcTemplate
) : AnalysisDao {

    fun getQuery(defaultTable: String, returnColumns: String?): Query {
        return Query(jdbcTemplate, defaultTable, returnColumns)
    }


    override fun analysis(startDate: LocalDate, endDate: LocalDate, clientId: Int, memberIds: List<Int>?, sort: MemberAnalysisSort, size: Int): List<MemberValue.AnalysisData> {


        val idsStr = if (memberIds.isNullOrEmpty()) {
            ""
        } else {
            " and member_id in (${memberIds.joinToString(separator = ",")})"
        }

        when (sort) {

            MemberAnalysisSort.WithdrawMax -> {
                """
                    select * from (
                        select member_id, sum(money) v from withdraw where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()
                        .let { listOf(it) }
            }
            MemberAnalysisSort.WithdrawSeqMax -> {
                """
                    select * from (
                        select member_id, count(id) v from withdraw where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()
                        .let { listOf(it) }
            }
            MemberAnalysisSort.DepositMax -> {
                val a = """
                    select * from (
                        select member_id, sum(money) v from deposit where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()


                val b = """
                    select * from (
                        select member_id, count(id) v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()

                listOf(a, b)
            }
            MemberAnalysisSort.DepositSeqMax -> {
                val a = """
                    select * from (
                        select member_id, sum(amount)  v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()


                val b = """
                    select * from (
                        select member_id, count(id) v from pay_order where created_time > ? and created_time < ? and client_id = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()

                listOf(a, b)

            }
            MemberAnalysisSort.WinMax -> {
                """
                    select * from (
                        select member_id, count(id) v from member_daily_report where day >= ? and day < ? and client_xid = ? $idsStr group by member_id 
                    ) t order by v limit $size
                """.trimIndent()
                        .let { listOf(it) }


            }
            MemberAnalysisSort.LossMax -> {

            }
            MemberAnalysisSort.PromotionMax -> {

            }
        }



        TODO("Not yet implemented")
    }
}