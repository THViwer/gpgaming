package com.onepiece.treasure.games.live.golddeluxe

import com.onepiece.treasure.beans.model.token.DefaultClientToken

class GoldDeluxeBuild private constructor(
) {

    companion object {

        fun instance(token: DefaultClientToken, method: String): GoldDeluxeBuild {

            return GoldDeluxeBuild()
        }
    }

}