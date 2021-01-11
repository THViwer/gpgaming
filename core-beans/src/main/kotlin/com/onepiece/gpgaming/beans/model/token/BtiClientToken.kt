package com.onepiece.gpgaming.beans.model.token

class BtiClientToken(

        val apiPath: String = "https://whlapi3.bti360.io/WHLCustomers.asmx",

        val orderApiPath: String = "https://dataapi-tw.btisports.io/dataapi",

        val gamePath: String = "https://stg20152-28712303.core-tech.dev",

        val agentUsername: String = "RTIGGroup_jay888",

        val agentPassword: String = "c#VNJHh8!fsy(Fe6",

        val currencyCode: String = "MYR",

        val countryCode: String = "MY"

) : ClientToken