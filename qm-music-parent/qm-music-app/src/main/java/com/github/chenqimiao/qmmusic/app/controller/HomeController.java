package com.github.chenqimiao.qmmusic.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 22:06
 **/
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "forward:/login.html";
    }
}