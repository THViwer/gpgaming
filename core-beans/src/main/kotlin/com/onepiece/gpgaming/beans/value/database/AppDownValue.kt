package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status

sealed class AppDownValue {


    data class Update(
            val id: Int,

            val status: Status?,

            val iosPath: String?,

            val androidPath: String?
    )


}