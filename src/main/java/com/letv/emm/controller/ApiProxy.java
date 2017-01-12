package com.letv.emm.controller;

import com.letv.emm.services.ApiProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping(path = "/LeMobile/proxy/api")
public class ApiProxy {
    @Autowired
    ApiProxyService apiProxyService;

    @RequestMapping(value = "/request")
    @ResponseBody
    public void request(HttpServletRequest request, HttpServletResponse response) {
        apiProxyService.invokeApiProxy(request, response);
    }
}
