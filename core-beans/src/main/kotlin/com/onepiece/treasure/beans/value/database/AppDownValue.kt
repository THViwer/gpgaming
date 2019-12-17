package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Status

sealed class AppDownValue {


    data class Update(
            val id: Int,

            val status: Status?,

            val iosPath: String?,

            val androidPath: String?
    )


}