package com.onepiece.gpgaming.beans.enums

enum class PlatformCategory {

    // 捕鱼
    Fishing,

    // 体育
    Sport,

    // 真人视讯
    LiveVideo,

    // 老虎机
    Slot;


    fun getPromotionCategory(): PromotionCategory {
        return when (this) {
            Fishing -> PromotionCategory.Fishing
            Sport -> PromotionCategory.Sport
            LiveVideo -> PromotionCategory.Live
            Slot -> PromotionCategory.Slot
        }
    }

}