package com.onepiece.treasure.games.live.golddeluxe

sealed class GoldDeluxeValue {


    /**
     *   <Header>
    <Method>cCreateMember</Method>
    <MerchantID>1235</MerchantID>
    <MessageID>M110830134512K9n8d</MessageID>
    </Header>
     */
    data class Header(

            val method: String,

            val merchantID: String,

            val messageID: String

    )

}