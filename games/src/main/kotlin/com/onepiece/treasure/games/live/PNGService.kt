package com.onepiece.treasure.games.live

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.games.http.OkHttpUtil

class PNGService {



}


fun main() {

    val data = """
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
          <soapenv:Header/>  
          <soapenv:Body> 
            <v1:RegisterUser> 
              <v1:UserInfo> 
                <v1:ExternalUserId>1234</v1:ExternalUserId>  
                <v1:Username>TestUser</v1:Username>  
                <v1:Nickname>TestUser123</v1:Nickname>  
                <v1:Currency>EUR</v1:Currency>  
                <v1:Country>SE</v1:Country>  
                <v1:Birthdate>1977- 07-07</v1:Birthdate>  
                <v1:Registration>1978-08- 08</v1:Registration>  
                <v1:BrandId>TestBrand</v1:BrandId>  
                <v1:Language>sv_SE</v1:Language>  
                <v1:IP>1253456</v1:IP>  
                <v1:Locked>false</v1:Locked>  
                <v1:Gender>m</v1:Gender> 
              </v1:UserInfo> 
            </v1:RegisterUser> 
          </soapenv:Body> 
        </soapenv:Envelope>
    """.trimIndent()

    val objectMapper = jacksonObjectMapper()
    val xmlMapper = XmlMapper()

    val okHttpUtil = OkHttpUtil(objectMapper = objectMapper, xmlMapper = xmlMapper)







}