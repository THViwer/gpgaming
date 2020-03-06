package com.onepiece.gpgaming.beans.enums

import com.onepiece.gpgaming.beans.SystemConstant

enum class Bank(
        val cname: String,
        val logo: String,
        val grayLogo: String
) {

    MBB("MAYBANK", "${SystemConstant.AWS_BANK_LOGO_URL}/MBB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/MBB_withdrawGray.png"),

    CIMB("CIMB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB_withdrawGray.png"),

    HLB("HONG LEONG", "${SystemConstant.AWS_BANK_LOGO_URL}/HLB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/HLB_withdrawGray.png"),

    PBB("PUBLIC BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/PBB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/PBB_withdrawGray.png"),

    RHB("RHB Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/RHB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/RHB_withdrawGray.png"),

    AMB("AMBANK", "${SystemConstant.AWS_BANK_LOGO_URL}/AMB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/AMB_withdrawGray.png"),

    UOB("UOB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/UOB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/UOB_withdrawGray.png"),

    RAKYAT("BANK RAKYAT", "${SystemConstant.AWS_BANK_LOGO_URL}/BR_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/BR_withdrawGray.png"),

    OCBC("OCBCBANK", "${SystemConstant.AWS_BANK_LOGO_URL}/OCBC_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/OCBC_withdrawGray.png"),

    HSBC("HSBC", "${SystemConstant.AWS_BANK_LOGO_URL}/HSBC_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/HSBC_withdrawGray.png"),

    ISLAM("BANK ISLAM", "${SystemConstant.AWS_BANK_LOGO_URL}/BIMB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/BIMB_withdrawGray.png"),

    AFFIN("AFFIN BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/AFFIN_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/AFFIN_withdrawGray.png"),

    ALLIANCE("ALLIANCE BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/ALLIANCE_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/ALLIANCE_withdrawGray.png"),

    BSN("BANK SIMPANAN", "${SystemConstant.AWS_BANK_LOGO_URL}/BSN_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/BSN_withdrawGray.png"),

    CITI("CITIBANK", "${SystemConstant.AWS_BANK_LOGO_URL}/CITI_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/CITI_withdrawGray.png"),

    SCB("Standard Chater", "${SystemConstant.AWS_BANK_LOGO_URL}/SCB_withdraw.png", "${SystemConstant.AWS_BANK_LOGO_URL}/SCB_withdrawGray.png"),



//    BSN("BSN", "${SystemConstant.AWS_BANK_LOGO_URL}/BSN.jpeg"),
//
//    CIMB("CIMB Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB.jpeg"),
//
//    HongLeong("HongLeong Bank", "${SystemConstant.AWS_BANK_LOGO_URL}/HongLeong.jpeg"),
//
//    May("MayBank", "${SystemConstant.AWS_BANK_LOGO_URL}/May.jpeg"),
//
//    PUBLIC("PUBLIC BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/PUBLIC.jpeg"),
//
//    RHB("RHB BANK", "${SystemConstant.AWS_BANK_LOGO_URL}/RHB.jpeg")


}