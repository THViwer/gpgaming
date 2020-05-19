package com.onepiece.gpgaming.beans.value.database

sealed class AnalysisValue {

    data class ActiveCollect(

            val agentId: Int,

            val activeCount: Int

    )

}