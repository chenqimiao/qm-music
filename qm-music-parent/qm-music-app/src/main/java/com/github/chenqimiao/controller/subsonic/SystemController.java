package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.response.subsonic.SubsonicPong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 15:54
 **/
@RestController
@RequestMapping(value = "/rest")
public class SystemController {

    @GetMapping(value = "/ping")
    public SubsonicPong ping() {
        return new SubsonicPong();
    }
}
