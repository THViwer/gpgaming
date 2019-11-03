package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Announcement
import com.onepiece.treasure.beans.value.database.AnnouncementCo
import com.onepiece.treasure.beans.value.database.AnnouncementUo

interface AnnouncementService {

    fun last(clientId: Int): Announcement?

    fun all(clientId: Int): List<Announcement>

    fun create(announcementCo: AnnouncementCo)

    fun update(announcementUo: AnnouncementUo)


}