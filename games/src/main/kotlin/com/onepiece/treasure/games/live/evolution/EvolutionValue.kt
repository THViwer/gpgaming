package com.onepiece.treasure.games.live.evolution

sealed class EvolutionValue {


    data class RegisterPlayer(

            val uuid: String,

            val player: Player,

            val config: Config

    )

    data class Player(
            val id: String,

            val update: Boolean = true,

            val firstName: String = id,

            val lastName: String = id,

            val nickname: String = id,

            val country: String = "MY",

            val language: String = "en",

            val currency: String = "MYR",

            val session: Session
    ) {

        data class Session(

                val id: String,

                val ip: String
        )

    }

    data class Config(

            val brand: Brand = Brand(),

            val game: Game = Game(),

            val channel: Channel = Channel(),

            val urls: Urls = Urls(cashier = null, responsibleGaming = null, lobby = null, sessionTimeout = null)
    ) {
        data class Brand(
                val id: String = "1",

                val skin: String = "1"
        )

        data class Game(
                // 热门游戏
                val category: String = "TopGames",

                val table: Table = Table()
        ) {

            data class Table(
                    //TODO 问阿牛
                    val id: String = "leqhceumaq6qfoug"
            )

        }

        data class Channel(

                val wrapped: Boolean = false,

                val mobile: Boolean = true
        )
    }

    data class Urls(

            val cashier: String?,

            val responsibleGaming: String?,

            val lobby: String?,

            val sessionTimeout: String?
    )


    data class GetUrlOrCreateUser(
            val entry: String,

            val entryEmbedded: String
    )
}