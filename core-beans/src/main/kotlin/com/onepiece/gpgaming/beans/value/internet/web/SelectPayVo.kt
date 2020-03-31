package com.onepiece.gpgaming.beans.value.internet.web

data class SelectPayVo (

        // 银行列表
        val banks: List<ClientBankVo>,

        // 第三方支付列表
        val thirdPays: List<ThirdPayValue.SupportPay>
)