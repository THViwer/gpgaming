package com.onepiece.gpgaming.beans.value.internet.web

sealed class SalesmanValue {

    data class SaleInfo(

            // 名称
            val name: String,

            // 推广码
            val saleCode: String,

            // 推广连接
            val saleLink: String,

            // 从未电话数量
            val neverCallCount: Int,

            // 今日电话数量
            val todayCallCount: Int

    )

    data class MemberAllocateInfo(

            val ownMemberCount: Int,

            val systemMemberCount:  Int

    )

}