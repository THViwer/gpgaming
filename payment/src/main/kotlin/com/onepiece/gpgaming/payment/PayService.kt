package com.onepiece.gpgaming.payment

interface PayService {

    fun start(req: PayRequest): Map<String,  Any>

}