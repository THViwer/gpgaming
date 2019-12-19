//package com.onepiece.treasure.core.dao.impl
//
//import com.onepiece.treasure.beans.model.Announcement
//import com.onepiece.treasure.beans.value.database.AnnouncementCo
//import com.onepiece.treasure.beans.value.database.AnnouncementUo
//import com.onepiece.treasure.core.dao.AnnouncementDao
//import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
//import org.springframework.stereotype.Repository
//import java.sql.ResultSet
//import java.time.LocalDateTime
//
//@Repository
//class AnnouncementDaoImpl : BasicDaoImpl<Announcement>("announcement"), AnnouncementDao {
//
//    override val mapper: (rs: ResultSet) -> Announcement
//        get() = { rs ->
//            val id = rs.getInt("id")
//            val clientId = rs.getInt("client_id")
//            val createdTme = rs.getTimestamp("created_time").toLocalDateTime()
//            val updateTime = rs.getTimestamp("updated_time").toLocalDateTime()
//            Announcement(id = id, clientId = clientId, createdTime = createdTme, updatedTime = updateTime)
//        }
//
//    override fun create(announcementCo: AnnouncementCo): Int {
//        return insert()
//                .set("client_id", announcementCo.clientId)
//                .executeGeneratedKey()
//    }
//
////    override fun update(announcementUo: AnnouncementUo): Boolean {
////        return update()
////                .set("updated_time", LocalDateTime.now())
////                .where("id", announcementUo.id)
////                .executeOnlyOne()
////    }
//}