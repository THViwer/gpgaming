package com.onepiece.treasure.su.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class IndexController {

    @GetMapping("/")
    @ApiIgnore
    public String index() {
        return "redirect:swagger-ui.html";
    }


}
