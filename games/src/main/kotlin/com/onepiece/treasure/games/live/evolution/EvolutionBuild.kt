package com.onepiece.treasure.games.live.evolution

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant

class EvolutionBuild private constructor() {

    val param = hashMapOf<String, Any>()

    companion object {

        fun instance(token: DefaultClientToken, cCode: String, username: String): EvolutionBuild {

            return EvolutionBuild()
                    .set("cCode", cCode)
                    .set("ecID", token.appId)
                    .set("euID", username)
        }
    }

    fun set(k: String, v: Any): EvolutionBuild {
        this.param[k] = v
        return this
    }

    fun build(path: String): String {
        val urlParam = param.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${GameConstant.EVOLUTION_API_URL}${path}?$urlParam"
    }

}