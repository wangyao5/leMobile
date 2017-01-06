package com.letv.emm.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping(path = "/proxy/api")
public class ApiProxy {

    @RequestMapping(value = "/request")
    @ResponseBody
    public String request(@RequestHeader(name = "url") String url,
                          HttpServletRequest request,
                          HttpServletResponse response) {


        return "ok";
    }
}
