package com.letv.emm.controller;

import com.letv.emm.services.AuthService;
import com.letv.emm.vo.AppVo;
import com.letv.emm.vo.SidVo;
import com.letv.emm.vo.StatusCode;
import com.letv.emm.vo.StatusVo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@CrossOrigin
@RequestMapping(path = "/LeMobile/aws/api")
@ConfigurationProperties(prefix="spring.aws.mobile")
public class AWSMobileAuth {
    @Autowired
    private AuthService authService;

    private String authUrl;

    private String appId;

    private String appSecret;

    private String grantType;

    @RequestMapping(value = "/auth/{ticket}", method = RequestMethod.POST)
    @ResponseBody
    public StatusVo auth(@PathVariable("ticket") String ticket, HttpServletRequest request) {
        StatusVo statusVo = new StatusVo();
        AppVo app = new AppVo();
        app.setAppId(appId);
        app.setGrantType(grantType);
        app.setAppSecret(appSecret);
        String accessToken = authService.getAccessToken(app);
        if (null == accessToken || accessToken.isEmpty()) {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("Ticket auth failed!");
            return statusVo;
        }
        String author = authService.getUserInfo(ticket, accessToken);
        JSONObject authorJson = new JSONObject(author);
        String mobile = authorJson.getString("mobile");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(authUrl)
                .append("?userId=")
                .append(mobile)
                .append("&pwd=pwd&bip=")
                .append(request.getRemoteHost())
                .append("&lang=cn");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost sidPost = new HttpPost(stringBuilder.toString());
        String sid = null;
        try {
            CloseableHttpResponse response = httpClient.execute(sidPost);
            sid = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        statusVo.setCode(StatusCode.SUCCESS.ordinal());

        SidVo sidObj = new SidVo();
        try {
            sid = URLEncoder.encode(sid, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sidObj.setSid(sid);
        System.out.println("sid="+sid);
        statusVo.setBody(sidObj);
        return statusVo;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
