//package com.onepiece.treasure.web.controller
//
//import com.onepiece.treasure.beans.value.database.WebSiteCo
//import com.onepiece.treasure.beans.value.database.WebSiteUo
//import com.onepiece.treasure.beans.value.internet.web.*
//import com.onepiece.treasure.web.controller.basic.BasicController
//import com.onepiece.treasure.core.service.WebSiteService
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/webSite")
//class WebSiteApiController(
//        private val webSiteService: WebSiteService
//): BasicController(), WebSiteApi {
//
//    @GetMapping
//    override fun all(): List<WebSiteVo> {
////        return DomainValueFactory.generatorWebSites()
//        return webSiteService.all(clientId).map {
//            with(it) {
//                WebSiteVo(id = id, domain = domain, status = status, createdTime = createdTime)
//            }
//        }
//    }
//
//    @PostMapping
//    override fun create(@RequestBody webSiteCoReq: WebSiteCoReq) {
//        val webSiteCo = WebSiteCo(clientId = clientId, domain = webSiteCoReq.domain)
//        webSiteService.create(webSiteCo)
//    }
//
//    @PutMapping
//    override fun update(@RequestBody webSiteUoReq: WebSiteUoReq) {
//        val webSiteUo = WebSiteUo(id = webSiteUoReq.id, status = webSiteUoReq.status, domain = webSiteUoReq.domain)
//        webSiteService.update(webSiteUo)
//    }
//}