package com.github.xuchengen.xdns.web;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 登录控制器<br>
 * 作者: 徐承恩<br>
 * 邮箱: xuchengen@gmail.com<br>
 * 日期: 2022-04-09 15:23<br>
 **/
@Controller
public class LoginCtl {

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public String doLogin(@RequestParam String userName, @RequestParam String password, @RequestParam(required = false) String code) {
        return "OK";
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<byte[]> captcha(HttpSession session) {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(180, 70, 4, 0);
        session.setAttribute("LOGIN:CAPTCHA", captcha.getCode());
        byte[] imageBytes = captcha.getImageBytes();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).contentLength(imageBytes.length).body(imageBytes);
    }
}
