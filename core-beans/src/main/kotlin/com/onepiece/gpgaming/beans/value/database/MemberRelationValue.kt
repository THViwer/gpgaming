package com.onepiece.gpgaming.beans.value.database

sealed class MemberRelationValue {

    data class MemberRelationCo(

            // bossId
            val bossId: Int,

            // 会员Id
            val memberId: Int,

            // r1 代理
            val r1: Int,

            // r2 代理
            val r2: Int?
    )

}