package com.letv.emm.services;

import com.letv.emm.vo.AppVo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {
    private static final String URL = "http://msg.lecommons.com/openauth2/api";

    public String getAccessToken(AppVo app) {
        String responseBody = doGet(getAccessTokenUrl(app));
        JSONObject tokenJsonObj = null;
        try {
            tokenJsonObj = new JSONObject(responseBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != tokenJsonObj) {
            return tokenJsonObj.getString("access_token");
        }
        return null;
    }

    public String getUserInfo(String ticket, String accessToken) {
        return doGet(getUserInfoUrl(ticket, accessToken));
    }

    private String doGet(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet AccessTokenGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        String accessBody = "";
        try {
            response = httpClient.execute(AccessTokenGet);
            accessBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return accessBody;
    }

    private String getAccessTokenUrl(AppVo app) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL)
                .append("/token?grant_type=")
                .append(app.getGrantType())
                .append("&appid=")
                .append(app.getAppId())
                .append("&secret=")
                .append(app.getAppSecret());

        return stringBuilder.toString();
    }

    private String getUserInfoUrl(String ticket, String accssToken) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL)
                .append("/getcontext?ticket=")
                .append(ticket)
                .append("&access_token=")
                .append(accssToken);
        return stringBuilder.toString();
    }
}
