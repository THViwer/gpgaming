package com.onepiece.gpgaming.beans.value.internet.web

import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.CellData
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.property.ExcelContentProperty
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.RiskLevel
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Deposit
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.model.Wallet
import com.onepiece.gpgaming.beans.model.Withdraw
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


sealed class MemberValue {

        data class RiskDetail(

                // 姓名相同列表
                val sameNameList: List<RiskMemberVo>,

                // 注册ip相同列表
                val sameRegisterIpList: List<RiskMemberVo>

        ) {

                data class RiskMemberVo(

                        // 用户Id
                        val memberId: Int,

                        // 用户名
                        val username: String,

                        // 姓名
                        val name:  String,

                        // 登陆ip
                        val loginIp: String,

                        // 注册ip
                        val registerIp: String
                )

        }

        data class FollowVo(

                @ExcelProperty("会员Id")
                @ApiModelProperty("会员Id")
                val memberId: Int,

                @ExcelProperty("用户名")
                @ApiModelProperty("用户名")
                val username: String,

                @ExcelProperty("电话")
                @ApiModelProperty("电话")
                val phone: String,



                @ExcelProperty("充值金额")
                @ApiModelProperty("充值金额")
                val depositMoney: BigDecimal,

                @ExcelProperty("充值次数")
                @ApiModelProperty("充值次数")
                val depositCount: Int,

//                @ExcelIgnore
                @ExcelProperty("最后登陆时间", converter = LocalDateTimeConverter::class)
                @ApiModelProperty("最后一次充值时间")
                val lastDepositTime: LocalDateTime?,

//                @ExcelProperty("最后一次充值时间")
//                @JsonIgnore
//                val dLastDepositTime: Date? = lastDepositTime?.let { Date.from(it.atZone(ZoneId.of("Asia/Shanghai")).toInstant()) },

                @ApiModelProperty("几天未存款)")
                @ExcelProperty("几天未存款")
                val depositDay: Int? = lastDepositTime?.let {
                        Duration.between(it, LocalDateTime.now()).toDays()
                }?.toInt(),



                @ExcelProperty("取款金额")
                @ApiModelProperty("取款金额")
                val withdrawMoney:  BigDecimal,

                @ExcelProperty("取款次数")
                @ApiModelProperty("取款次数")
                val withdrawCount: Int,

//                @ExcelIgnore
                @ExcelProperty("最后一次取款时间", converter = LocalDateTimeConverter::class)
                @ApiModelProperty("最后一次取款时间")
                val lastWithdrawTime:  LocalDateTime?,

//                @ExcelProperty("最后一次取款时间")
//                @JsonIgnore
//                val dLastWithdrawTime: Date? = lastWithdrawTime?.let { Date.from(it.atZone(ZoneId.of("Asia/Shanghai")).toInstant()) },

                @ApiModelProperty("几天未取款)")
                @ExcelProperty("几天未取款")
                val withdrawDays: Int? = lastWithdrawTime?.let {
                        Duration.between(it, LocalDateTime.now()).toDays()
                }?.toInt(),




//                @ExcelIgnore
                @ExcelProperty("注册时间", converter = LocalDateTimeConverter::class)
                @ApiModelProperty("注册时间")
                val registerTime: LocalDateTime,

//                @ExcelProperty("注册时间")
//                @JsonIgnore
//                val dRegisterTime: Date? = registerTime.let {
//                        Date.from(it.atZone(ZoneId.of("Asia/Shanghai")).toInstant())
//                },



//                @ExcelIgnore
                @ExcelProperty("最后登陆时间", converter = LocalDateTimeConverter::class)
                @ApiModelProperty("最后登陆时间")
                val lastLoginTime:  LocalDateTime?,

//                @ExcelProperty("最后登陆时间")
//                @JsonIgnore
//                val dLastLoginTime: Date? = lastLoginTime?.let { Date.from(it.atZone(ZoneId.of("Asia/Shanghai")).toInstant()) },

                @ExcelProperty("几天未登陆")
                @ApiModelProperty("几天未登陆)")
                val neverLoginDays: Int? = lastLoginTime?.let {
                        Duration.between(it, LocalDateTime.now()).toDays()
                }?.toInt()

        )


        data class Agent(
                // id
                val id: Int,

                // 上级代理Id
                val superiorAgentId: Int,

                // 上级代理用户名
                val superiorUsername: String,

                // 代理Id
                val agentId: Int,

                // 用户名
                val username: String,

                // 姓名
                val name: String,

                // 手机号
                val phone: String,

                // 状态
                val status: Status,

                // 创建时间
                val createdTime: LocalDateTime,

                // 登陆Ip
                val loginIp: String?,

                // 推广码
                val promoteCode: String,

                // 登陆时间
                val loginTime: LocalDateTime?

        )

        data class AgentUo(

                // 代理Id
                val id: Int,

                // 真实姓名
                val name: String?,

                // 代理佣金
                val agencyMonthFee: BigDecimal,

                // 修改密码
                val password: String?,

                // 状态
                val status: Status?
        )

}

open class LocalDateTimeConverter: Converter<LocalDateTime> {

        override fun supportJavaTypeKey(): Class<*> {
                return LocalDateTime::class.java
        }

        override fun supportExcelTypeKey(): CellDataTypeEnum {
                return CellDataTypeEnum.STRING
        }

        override fun convertToExcelData(p0: LocalDateTime?, p1: ExcelContentProperty?, p2: GlobalConfiguration?): CellData<*> {
//                val date = p0?.let {
//                        Date.from(it.atZone(ZoneId.of("Asia/Shanghai")).toInstant())
//                }
                return CellData<Date>(p0?.toString())
        }

        override fun convertToJavaData(p0: CellData<*>?, p1: ExcelContentProperty?, p2: GlobalConfiguration?): LocalDateTime {
                return LocalDateTime.now()
        }
}


data class MemberPage(

        val data: List<MemberVo>,

        val total: Int
)

data class MemberVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("代理Id")
        val agentId: Int,

        @ApiModelProperty("代理用户名")
        val agentUsername: String = "-",

        @ApiModelProperty("电销Id")
        val saleId: Int,

        @ApiModelProperty("电销用户名")
        val saleUsername: String,

        @ApiModelProperty("国家")
        val country: Country,

        @ApiModelProperty("推广码")
        val promoteCode: String?,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("层级Id")
        val levelId: Int,

        @ApiModelProperty("层级名称")
        val level: String,

        @ApiModelProperty("手机号")
        val phone: String,

//        @ApiModelProperty("姓名")
//        val name: String,

        @ApiModelProperty("余额(中心钱包)")
        val balance: BigDecimal,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("登陆Ip")
        val loginIp: String?,

        @ApiModelProperty("登陆Ip")
        val registerIp: String,

        @ApiModelProperty("风险等级")
        val riskLevel: RiskLevel,

        @ApiModelProperty("登陆时间")
        val loginTime: LocalDateTime?,


        @ApiModelProperty("邮箱地址")
        val email: String?,

        @ApiModelProperty("生日")
        val birthday: LocalDate?,

        @ApiModelProperty("身份证")
        val idCard: String?,

        @ApiModelProperty("地址")
        val address: String?
)

data class MemberWalletInfo(

        // id
        val memberId: Int,

        // 钱包详情
        val wallet: Wallet,

        // 平台余额列表
        val balances: List<BalanceVo>,

        // 最后5个充值信息
        val lastFiveDeposit: List<Deposit>,

        // 三方充值列表
        val lastPayOrders: List<PayOrder>,

        // 最后5个取款信息
        val lastFiveWithdraw: List<Withdraw>

) {

        data class BalanceVo(

                // 平台用户名
                val pusername: String,

                // 平台密码
                val ppassword: String,

                // 平台
                val platform: Platform,

                // 当前余额
                val balance: BigDecimal,

                // 总打码量
                val totalBet: BigDecimal,

                // 总盈利
                val totalWin: BigDecimal,

                // 总充值金额
                val totalAmount: BigDecimal,

                // 总出款金额
                val totalTransferOutAmount: BigDecimal,

                // 总优惠金额
                val totalPromotionAmount: BigDecimal
        )
}

data class MemberUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("电销Id")
        val saleId: Int?,

        @ApiModelProperty("姓名")
        val name: String?,

        @ApiModelProperty("修改手机号")
        val phone: String?,

        @ApiModelProperty("层级Id")
        val levelId: Int?,

        @ApiModelProperty("密码")
        val password: String?,

        @ApiModelProperty("状态")
        val status: Status?,

        @ApiModelProperty("生日")
        val birthday: LocalDate?,

        @ApiModelProperty("身份证")
        val idCard: String?,

        @ApiModelProperty("地址")
        val address: String?,

        @ApiModelProperty("邮箱地址")
        val email: String?

)

data class MemberCoReq(

        @ApiModelProperty("代理Id")
        val agentId: Int?,

        @ApiModelProperty("角色")
        val role: Role = Role.Member,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("姓名")
        val name: String,

        @ApiModelProperty("phone")
        val phone: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("安全密码")
        val safetyPassword: String,

        @ApiModelProperty("层级")
        val levelId: Int,

        @ApiModelProperty("推广码")
        val promoteCode: String?,

        @ApiModelProperty("电销人员Id")
        val saleCode: String?,

        @ApiModelProperty("生日")
        val birthday: LocalDate?,

        @ApiModelProperty("邮箱地址")
        val email: String?

)