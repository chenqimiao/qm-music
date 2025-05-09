package com.github.chenqimiao.qmmusic.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@Controller
@RequestMapping("/api/system")
public class WebStatusController {

    @RequestMapping("/status")
    @ResponseBody
    public String status() {
        return "SUCCESS";
    }
}
