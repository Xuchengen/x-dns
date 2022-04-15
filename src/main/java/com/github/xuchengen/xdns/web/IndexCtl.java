package com.github.xuchengen.xdns.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexCtl {

    @GetMapping(value = "/")
    public String index() {
        return "index";
    }

}
