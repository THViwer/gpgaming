package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.value.database.OtherPlatformReportValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberValue
import com.onepiece.gpgaming.core.dao.OtherPlatformReportDao
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.games.GameApi
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * 其它平台的定时任务: Kiss918、Pussy888、Mega
 */
@Component
class OtherPlatformTask(
        private val platformBindService: PlatformBindService,
        private val otherPlatformReportDao: OtherPlatformReportDao,
        private val gameApi: GameApi,
        private val platformMemberService: PlatformMemberService
) {

    private val list = listOf(Platform.Kiss918, Platform.Pussy888, Platform.Mega)

        @Scheduled(cron = "0 30 0 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    fun start() {
        val binds = platformBindService.all()
                .filter {
                    it.platform == Platform.Kiss918 || it.platform == Platform.Pussy888 || it.platform == Platform.Mega
                }.filter { it.clientId == 1}

        val startDate = LocalDate.now().minusDays(1)

        binds.forEach { bind ->
            val list = gameApi.queryReport(clientId = bind.clientId, platform = bind.platform, startDate = startDate)
            if (list.isEmpty()) return@forEach

            val usernames = list.map { it.username }
            val memberQuery = PlatformMemberValue.PlatformMemberQuery(clientId = bind.clientId, platform = bind.platform, usernames = usernames)
            val members = platformMemberService.list(memberQuery)
                    .map { it.username to it }
                    .toMap()

            val data = list.mapNotNull { data ->
                members[data.username]?.let { member ->
                    OtherPlatformReportValue.PlatformReportCo(clientId = member.clientId, memberId = member.memberId, bet = data.bet,
                            win = data.win, platform = data.platform, originData = data.originData, day = startDate)
                }
            }
            if (data.isNotEmpty()) {
                otherPlatformReportDao.batch(data)
            }
        }
    }

}