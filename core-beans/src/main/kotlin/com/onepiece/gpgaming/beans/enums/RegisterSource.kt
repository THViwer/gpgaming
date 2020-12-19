package com.onepiece.gpgaming.beans.enums

enum class RegisterSource(
        private val code: Int
) {

    // 自己
    Own(1),

    // 代理
    Agent(2),

    // 会员介绍
    Introduce(3),

    // 营销
    Market(4),

    // 电销
    Sale(5),

    ;

    companion object {

        fun getSourceByCode(code: Int): RegisterSource {
            return when (code) {
                1 -> Own
                2 -> Agent
                3 -> Introduce
                4 -> Market
                5 -> Sale
                else -> Own
            }
        }

        fun splice(source: RegisterSource, id: Int): String {
            return "${source.code}$id"
        }

        fun split(affid: String): Pair<RegisterSource, Int> {
            val code = affid.substring(0, 1).toInt()
            val id = affid.substring(1, affid.length).toInt()

            return getSourceByCode(code = code) to id
        }

    }
}