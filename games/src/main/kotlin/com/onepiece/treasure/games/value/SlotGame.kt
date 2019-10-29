package com.onepiece.treasure.games.value

data class SlotGame(

        val gameId: String,

        val gameName: String,

        val platforms: List<GamePlatform>,

        val specials: List<Special>,

        val icon: String

) {

    enum class GamePlatform {
        PC,

        Mobile
    }

    enum class Special {
        Hot,

        New
    }
}