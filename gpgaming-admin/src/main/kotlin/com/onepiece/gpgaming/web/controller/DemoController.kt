package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.WaiterService
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
class DemoController(
        private val gamePlatformService: GamePlatformService,
        private val platformBindService: PlatformBindService,
        private val waiterService: WaiterService,
        private val memberService: MemberService,
        private val memberInfoService: MemberInfoService,
        private val jdbcTemplate: JdbcTemplate
) {

//    @GetMapping("/image")
//    fun asyncImage() {
//
//        val gamePlatforms = gamePlatformService.all()
//        val gamePlatformMap =  gamePlatforms.map { it.platform to it }.toMap()
//
//        val binds = platformBindService.all()
//
//        binds.parallelStream().forEach {
//            val gamePlatform = gamePlatformMap[it.platform] ?: error("")
//            val bindUo = with(gamePlatform) {
//                PlatformBindUo(id = it.id, name = name, mobileIcon = mobileIcon, mobileDisableIcon = mobileDisableIcon, originIcon = originIcon,
//                originIconOver = originIconOver, platformDetailIconOver = platformDetailIconOver, platformDetailIcon = platformDetailIcon,
//                icon = icon, disableIcon = disableIcon)
//            }
//            platformBindService.update(bindUo)
//        }
//
//    }


    @GetMapping("/info/init")
    fun initMemberInfo() {

        val memberQuery = MemberQuery()
        val list = memberService.list(memberQuery)

        list.forEach {
            val infoQuery = MemberInfoValue.MemberInfoQuery(bossId = it.bossId, clientId = it.clientId, memberId = it.id)
            val infos = memberInfoService.list(infoQuery)
            if (infos.isNotEmpty()) return@forEach

            val infoUo = MemberInfoValue.MemberInfoUo(memberId = it.id, saleId = it.saleId)
            memberInfoService.asyncUpdate(infoUo)
        }
    }

    @GetMapping("/allocation")
    fun allocation(
            @RequestParam("clientId") clientId: Int
    ) {
        val sales = waiterService.all(role = Role.Sale)
                .filter { it.status == Status.Normal }
                .filter { it.clientId == clientId }
                .map { it.id }


        val memberQuery = MemberQuery(clientId = clientId)
        val list = memberService.list(memberQuery = memberQuery)


        val size = sales.size
        list.map {
            val x = it.id % size
            TD(saleId = sales[x], memberId = it.id)
        }.groupBy {
            it.saleId
        }.forEach {
            val saleId = it.key
            val memberIds = it.value.map { it.memberId }
            val sql = "update member set sale_id = $saleId where id in (${memberIds.joinToString(separator = ",")})"
            jdbcTemplate.update(sql)

            val sql2 = "update member_info set sale_id = $saleId where member_id in (${memberIds.joinToString(separator = ",")})"
            jdbcTemplate.update(sql2)
        }
    }

    @GetMapping("/logo/copy")
    fun copyIcon(
            @RequestParam("fid") fid: Int,
            @RequestParam("tid") tid: Int
    ) {

        val fList = platformBindService.findClientPlatforms(clientId = fid)
        val t2List = platformBindService.findClientPlatforms(clientId = tid)

        t2List.forEach { bind ->
            val copy = fList.firstOrNull { it.platform == bind.platform } ?: return@forEach

            val bindUo = PlatformBindUo(id = bind.id, icon = copy.icon, mobileIcon = copy.mobileIcon, mobileDisableIcon = copy.mobileDisableIcon,
                    disableIcon = copy.disableIcon, originIcon = copy.originIcon, originIconOver = copy.originIconOver, platformDetailIcon = copy.platformDetailIcon,
                    platformDetailIconOver = copy.platformDetailIconOver)
            platformBindService.update(bindUo)

        }
    }

    data class TD(
            val saleId: Int,

            val memberId: Int
    )


}