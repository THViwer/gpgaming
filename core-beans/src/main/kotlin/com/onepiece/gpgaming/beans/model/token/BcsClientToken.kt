package com.onepiece.gpgaming.beans.model.token

class BcsClientToken (

        val apiPath: String,

        val key: String,

        val currency: String,

        // new column
        val companyKey: String = "BCTP7Y7acC"

): ClientToken