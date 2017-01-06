package com.letv.emm.controller;

import com.letv.emm.services.AuthService;
import com.letv.emm.vo.AppVo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping(path = "/le/mobile/api")
public class LeMobileAuth {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/auth/{ticket}", method = RequestMethod.POST)
    @ResponseBody
    public String auth(@RequestBody AppVo app, @PathVariable("ticket") String ticket) {
        String accessToken = authService.getAccessToken(app);
        if (null == accessToken || accessToken.isEmpty()) {
            return "";
        }
        return authService.getUserInfo(ticket, accessToken);
    }

}
