package com.onepiece.gpgaming.mr.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.Parameter
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
@Profile("dev", "sit")
open class SwaggerConfig {

    @Bean
    open fun api(): Docket {

        val language = ParameterBuilder()
                .name("language")
                .description("language")
                .modelRef(ModelRef("com.onepiece.gpgaming.beans.enums.Language"))
                .parameterType("header")
                .defaultValue("CN")
                .build()

        val launch = ParameterBuilder()
                .name("launch")
                .description("launch")
                .modelRef(ModelRef("com.onepiece.gpgaming.beans.enums.LaunchMethod"))
                .parameterType("header")
                .defaultValue("Web")
                .build()

        val params = listOf<Parameter>(language, launch)


        return Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.onepiece.gpgaming.mr.controller"))
                .paths(PathSelectors.regex("^(?!auth).*$"))
                .build()
                .securitySchemes(securitySchemes())
//                .securityContexts(emptyList())
                .securityContexts(securityContexts())
                .globalOperationParameters(params)

    }

    private fun securitySchemes(): List<ApiKey> {
        return listOf(
                ApiKey("Authorization", "Authorization", "header")
//                ApiKey("language", "language", "header"),
//                ApiKey("launch", "launch", "header")
        )
    }

    private fun securityContexts(): List<SecurityContext> {
        val securityContext = SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build()
        return listOf(securityContext)

    }

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope

        return listOf(
                SecurityReference("Authorization", authorizationScopes)
//                SecurityReference("language", authorizationScopes),
//                SecurityReference("launch", authorizationScopes)
        )
    }


}