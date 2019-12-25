package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Status
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime


object WaiterValueFactory {

//    fun generatorWaiters(): List<WaiterVo> {
//
//        val now = LocalDateTime.now()
//
//        val w1 = WaiterVo(id = 1, username = "zhangsan", name = "张三", status = Status.Normal, createdTime = now, loginTime = now)
//        val w2 = w1.copy(id = 2, username = "lisi", name = "李四")
//        val w3 = w1.copy(id = 3, username = "wangwu", name = "王五")
//
//        return listOf(w1, w2, w3)
//    }

}

data class WaiterVo(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("名字")
        val name: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("创建时间")
        val createdTime: LocalDateTime,

        @ApiModelProperty("登陆Ip")
        val loginIp: String?,

        @ApiModelProperty("登陆时间")
        val loginTime: LocalDateTime?,

        @ApiModelProperty("入款银行卡Id")
        val clientBanks: List<ClientBankVo>?
) {
        data class ClientBankVo(

                @ApiModelProperty("银行卡Id")
                val id: Int,

                @ApiModelProperty("银行卡号")
                val clientCardNumber: String,

                @ApiModelProperty("银行卡姓名")
                val clientCardName: String
        )


}

data class WaiterUoReq(

        @ApiModelProperty("id")
        val id: Int,

        @ApiModelProperty("名字")
        val name: String?,

        @ApiModelProperty("状态")
        val status: Status?,

        @ApiModelProperty("密码")
        val password: String?,

        @ApiModelProperty("入款银行卡Id")
        val clientBanks: List<Int>?
)

data class WaiterCoReq(

        @ApiModelProperty("用户名")
        val username: String,

        @ApiModelProperty("密码")
        val password: String,

        @ApiModelProperty("名字")
        val name: String,

        @ApiModelProperty("状态")
        val status: Status,

        @ApiModelProperty("入款银行卡Id")
        val clientBanks: List<Int>?

)