package com.letv.emm.controller;

import com.letv.emm.services.AuthService;
import com.letv.emm.vo.AppVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
