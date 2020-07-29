package com.onepiece.gpgaming.beans.model.pay

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.Bank

class InstantPayConfig(

        val apiPath: String,

        val merchantCode: String,

        val currencyCode: String,

        val privateKey:  String,

        val publicKey: String,

        val supportBanks:  List<Bank>

) : PayConfig


//fun main() {
//    val config = InstantPayConfig(
//            apiPath = "https://directpay66.com",
//            merchantCode = "M741JDNU",
//            currencyCode = "MYR",
//            privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCrqHc6wiosPD0uKcqS0waV+tkl94SQxarC9axKQwm6LKwOT4EnYFgl0t47R9/JiULaFzJUnwC9GWA1Fo4ox9Kpxe/3DS8aRCZ5n3ftJtlYaiw9wG7tz+BQ4GuTlY7XIwsn6/a4kZ9s2LBvyCKJ4fI95r8sDuIdmT2fkzkJOdxO5c/fGkHcLcnJvvS/xdLQZJscCaFdMcz+MTzAsm3d0D+H1rzfd13hzo4AgHbh8FpWP4MGNaxTxh+i6lY+EGXvzcwk4vsPhx51fzI+8J/QGI/uwH/SdQJxpc8gj4Z5pDpfutkGJCrzcQEu/LYA17Kqcq47nLIicPOwN+xcVd9/jMUJAgMBAAECggEAF0+1gWFE4OT9WfuK6w2FY3DBdIp8kjyFyBwc3ajk9UeDxh296F8JGXndfCdymKfvUDa+OweszgFYM11wyND6JtwejmpX7zU6FGI3oxkXgw5IG8q6RclpKRKRCWXSFB1KMo2037PG9r0mmMeRCTCJ+SYQ6CbNRj+9QqJ3qG0XIg+SK4BHdEtWTRNXRKQP8xFBWWBydbGpIT3etbZgY0wqH2z0ryZKD3TJEh8j1jFpz6O3LzVu9NwwFXpn8CBwDP1boErKIr3OxvsXRBF2LHmG5c4Q0XwveMIB8UYIE96PgC3I9M9VuTP4Qqm6a7R7SMlaiJuGyZBMSejvbrWTTLlIQQKBgQDrp3ailNBjPlw0XryZ7ZQJKZstiqtajK67A+N7jFpfFpLtlGk13iUsk/M1Mmw/Pr17cx/hj4gEnQqEk5z88nCYWrr/E9G9bXI6T+sHoY9M0YBNiFnhhiumRPZ8W96F0y1tXihs+eMMK9gWIqr5Ej+w1qUIC9SVpEE+NVq/fXeEbQKBgQC6eogJ61rLb9cQOoLvRmsxkbuLdlyZtYlpW43+lMCfVRpFc+4FVQogrchW0U59Ucwo21KYMOFX1vmNBP0+CF5zQNMeOdcpb8cQlHOjhEL7G7oyEoFFXZIDfDeMTiKS37N4sqzbEY10azYyaID5pPO6fYe7bjHjGmzrX1RZ0LUJjQKBgQDoD9+6N0YHlW+Dew/LnbQ8Waag8H/SCdw/+pJYJl0QatCB+bap/3soLQJCDseeeJBsM3Df1VKjCGtFJ4Bfb/gFcMPZUZEoM9FbLzxKJYVSprTspSut9UyiziLVUDsQGmgP3bt9pY87eymukrE/2gKyiI24IL5WJozBUBVvoYTC+QKBgAaPArPenD3lfBntBOfngSOoLYF1M0KQuPmEp9TZZID2HEtvDWMQn5+QnSg4ilssAlM5i4kgxvww6mwETefdoXzKPeKlRCUH8mdp4TtSDo0IZ58VOYi4BseqmcWMhfjGd2h6WEwILQJ4NtrFBCxDmfLoMVrtQEHtjwEkRDI4ffWNAoGBAJ/dEmqV2SUWN+KYxN5XGm+2j8ZKpaPANM8x/QNnaiYd4Tz8rWTHogjK0aXzW/f+BHqMTJyRlQTnz4HFeu8DVQKOdXOmmgJArReFKCqvFIh6eHRchTW65cr0zl5csh1dgvrZk+Pt5EI/8+0Wjlu4g2Y8EZl011p30U8Njgjy16RW",
//            publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq6h3OsIqLDw9LinKktMGlfrZJfeEkMWqwvWsSkMJuiysDk+BJ2BYJdLeO0ffyYlC2hcyVJ8AvRlgNRaOKMfSqcXv9w0vGkQmeZ937SbZWGosPcBu7c/gUOBrk5WO1yMLJ+v2uJGfbNiwb8giieHyPea/LA7iHZk9n5M5CTncTuXP3xpB3C3Jyb70v8XS0GSbHAmhXTHM/jE8wLJt3dA/h9a833dd4c6OAIB24fBaVj+DBjWsU8YfoupWPhBl783MJOL7D4cedX8yPvCf0BiP7sB/0nUCcaXPII+GeaQ6X7rZBiQq83EBLvy2ANeyqnKuO5yyInDzsDfsXFXff4zFCQIDAQAB",
//            supportBanks = listOf(Bank.MBB, Bank.CIMB, Bank.RHB, Bank.PBB, Bank.HLB)
//    )
//
//    println(jacksonObjectMapper().writeValueAsString(config))
//
//}