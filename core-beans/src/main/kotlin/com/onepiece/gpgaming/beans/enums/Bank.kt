package com.onepiece.gpgaming.beans.enums

import com.onepiece.gpgaming.beans.SystemConstant

enum class Bank(
        val cname: String,

        val logo: String,
        val grayLogo: String,

        val mLogo: String,
        val mGrayLogo: String
) {

    // 马来西亚

    MBB(
            cname = "MAYBANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/MBB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/MBB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0006_MBB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/MBB_withdrawGray.png"
    ),

    CIMB(
            cname = "CIMB BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0010_CIMB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/CIMB_withdrawGray.png"

    ),

    HLB(
            cname = "HONG LEONG",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/HLB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/HLB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0008_HLB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/HLB_withdrawGray.png"
    ),

    PBB(
            cname = "PUBLIC BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/PBB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/PBB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0003_PBB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/PBB_withdrawGray.png"
    ),

    RHB(
            cname = "RHB Bank",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/RHB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/RHB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0002_RHB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/RHB_withdrawGray.png"
    ),

    AMB(
            cname = "AMBANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/AMB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AMB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0014_AMB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AMB_withdrawGray.png"
    ),

    UOB(
            cname = "UOB BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/UOB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/UOB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0000_UOB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/UOB_withdrawGray.png"
    ),

    RAKYAT(
            cname = "BANK RAKYAT",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/BR_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BR_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0012_BR_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BR_withdrawGray.png"
    ),

    OCBC(
            cname = "OCBCBANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/OCBC_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/OCBC_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0005_OCBC_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/OCBC_withdrawGray.png"
    ),

    HSBC(
            cname = "HSBC",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/HSBC_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/HSBC_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0007_HSBC_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/HSBC_withdrawGray.png"
    ),

    ISLAM(
            cname = "BANK ISLAM",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/BIMB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BIMB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0013_BIMB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BIMB_withdrawGray.png"
    ),

    AFFIN(
            cname = "AFFIN BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/AFFIN_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AFFIN_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0016_AFFIN_withdraw.pngg",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AFFIN_withdrawGray.png"
    ),

    ALLIANCE(
            cname = "ALLIANCE BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/ALLIANCE_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/ALLIANCE_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0015_ALLIANCE_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/ALLIANCE_withdrawGray.png"
    ),

    BSN(
            cname ="BANK SIMPANAN",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/BSN_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BSN_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0011_BSN_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/BSN_withdrawGray.png"
    ),

    CITI(
            cname = "CITIBANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/CITI_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/CITI_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0009_CITI_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/CITI_withdrawGray.png"
    ),

    SCB(
            cname = "Standard Chater",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/SCB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/SCB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/_0001_SCB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/SCB_withdrawGray.png"
    ),
    AGRO(
            cname = "agro bank",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/AGRO_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AGRO_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AGRO_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/AGRO_withdrawGray.png"
    ),


    // 新加坡


    // 新加坡
    DBS(
            cname = "DBS BANK",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/DBS_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/DBS_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/DBS_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/DBS_withdrawGray.png"
    ),

    POSB(
            cname = "POSB Bank",
            logo = "${SystemConstant.AWS_BANK_LOGO_URL}/POSB_withdraw.png",
            grayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/POSB_withdrawGray.png",
            mLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/POSB_withdraw.png",
            mGrayLogo = "${SystemConstant.AWS_BANK_LOGO_URL}/POSB_withdrawGray.png"
    ),


    ;

    companion object {

        fun of(country: Country): List<Bank> {
            return when (country) {
                Country.Malaysia -> {
                    listOf(MBB, CIMB, HLB, PBB, RHB, AMB, UOB, RAKYAT, OCBC, HSBC, ISLAM, AFFIN, ALLIANCE, BSN, CITI, SCB, AGRO)
                }
                Country.Singapore -> {
                    listOf(DBS, OCBC, UOB, POSB, CIMB, SCB, MBB)
                }
                else -> emptyList()
            }
        }
    }


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