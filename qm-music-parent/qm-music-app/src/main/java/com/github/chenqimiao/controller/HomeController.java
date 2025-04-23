package com.github.chenqimiao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 22:06
 **/
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "forward:/login.html";
    }
}