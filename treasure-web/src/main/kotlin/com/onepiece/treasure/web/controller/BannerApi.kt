package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.BannerCoReq
import com.onepiece.treasure.beans.value.internet.web.BannerUoReq
import com.onepiece.treasure.beans.value.internet.web.BannerVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["web setting"], description = "网站设置")
interface BannerApi  {

    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 列表")
    fun all(): List<BannerVo>

    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 创建")
    fun create(@RequestBody bannerCoReq: BannerCoReq)

    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody bannerUoReq: BannerUoReq)


}