package com.letv.emm.services;

import com.letv.emm.entity.PushPropEntity;
import com.letv.emm.repository.PushPropRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private PushPropRepository pushPropRepository;

    public boolean exists(String appId) {
        return pushPropRepository.exists(appId);
    }

    public PushPropEntity save(String appId, String communityNo, String pub, String pubk) {
        PushPropEntity pushPropEntity = new PushPropEntity();
        pushPropEntity.setAppId(appId);
        pushPropEntity.setCommunityNo(communityNo);
        pushPropEntity.setPub(pub);
        pushPropEntity.setPubk(pubk);
        pushPropEntity.setAppkey(getAppKey(appId, communityNo, pub, pubk));
        return pushPropRepository.save(pushPropEntity);
    }

    public PushPropEntity findByAppId(String appId) {
        return pushPropRepository.findOne(appId);
    }

    public PushPropEntity findByAppKey(String appKey) {
        return pushPropRepository.findByAppkey(appKey).get(0);
    }

    public void update(String appId, String communityNo, String pub, String pubk) {
        String appkey = getAppKey(appId, communityNo, pub, pubk);
        pushPropRepository.updatePushProp(appId, communityNo, pub, pubk, appkey);
    }

    public void deleteByAppId(String appId) {
        pushPropRepository.delete(appId);
    }

    public String getAppKey(String appId, String communityNo, String pub, String pubk) {
        String appInfoString = appId + communityNo + pub + pubk;
        return DigestUtils.sha1Hex(appInfoString);
    }

    public boolean sendTextMessage(String appkey, List<String> account) {
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), account);
        String url = "http://msg.lecommons.com/pubacc/pubsend";

        JSONObject msg = new JSONObject();
        msg.put("text", "已办最新数据测试");
        msg.put("url", "http://oa.test.lecommons.com/appweb/message.jsp?id=0");
        msg.put("appid", pushPropEntity.getAppId());
        msg.put("todo", "2");
        // msg.put("todoPriStatus", "undo");
        // msg.put("todoPriStatus", "done");
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 5);
        content.put("msg", msg);
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            System.out.println(content.toString());

            ContentType contentType = ContentType.create("application/json", "UTF-8");
            post.setEntity(new StringEntity(content.toString(), contentType));
            long t1 = System.currentTimeMillis();
            CloseableHttpResponse response = client.execute(post);
            long t2 = System.currentTimeMillis();
            System.out.println("耗时：" + (t2 - t1) / 1000);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("===================" + result);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return true;
    }

    public boolean sendLinkMessage(String appkey, List<String> account) {
        return true;
    }

    public boolean sendRichMessage(String appkey, List<String> account) {
        return true;
    }

    public boolean sendMessage(String appkey, List<String> account) {
        return true;
    }


    private JSONObject getFromJson(PushPropEntity pushPropEntity) {
        JSONObject from = new JSONObject();
        String no = pushPropEntity.getCommunityNo();// 工作圈号
        String pub = pushPropEntity.getPub();// 公共号编码
        String pubk = pushPropEntity.getPubk();// 公共号秘钥
        String time = Long.toString(System.currentTimeMillis());
        String nonce = Double.toString(Math.random()).substring(2);
        from.put("no", no);
        from.put("pub", pub);
        from.put("time", time);
        from.put("nonce", nonce);
        String[] data = new String[]{no, pub, pubk, nonce, time};
        Arrays.sort(data);
        from.put("pubtoken", DigestUtils.sha1Hex(StringUtils.join(Arrays.asList(data), null)));
        return from;
    }

    private JSONArray getTos(String communityNo, List<String> account) {
        JSONArray tos = new JSONArray();
        JSONObject to = new JSONObject();
        to.put("no", communityNo);
        if (null != account && account.size() > 0) {
            to.put("code", "all");
        } else {
            to.put("code", 2);
            to.put("user", account);
        }
        tos.put(to);
        return tos;
    }
}
