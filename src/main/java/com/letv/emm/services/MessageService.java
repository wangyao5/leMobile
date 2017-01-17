package com.letv.emm.services;

import com.letv.emm.entity.PushPropEntity;
import com.letv.emm.repository.PushPropRepository;
import com.letv.emm.vo.LinkMessageVo;
import com.letv.emm.vo.RichMessageItem;
import com.letv.emm.vo.RichMessageVo;
import com.letv.emm.vo.TextMessageVo;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@ConfigurationProperties(prefix = "spring.lecommons")
public class MessageService {

    private String pushUrl;

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

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

    public boolean sendLinkMessage(String appkey, int tabNo, LinkMessageVo message) {
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), message.getAccounts());
        JSONObject msg = getMessageJson();
        setTabNo(msg, tabNo);
        msg.put("text", message.getMessage());
        msg.put("appid", pushPropEntity.getAppId());
        msg.put("url", message.getUrl());
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 5);//文本链接消息
        content.put("msg", msg);
        return execHttp(content);
    }

    public boolean sendPubaccLinkMessage(String appkey, LinkMessageVo message) {
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), message.getAccounts());
        JSONObject msg = getPubaccMessageJson();
        msg.put("text", message.getMessage());
        msg.put("appid", pushPropEntity.getAppId());
        msg.put("url", message.getUrl());
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 5);//文本链接消息
        content.put("msg", msg);
        return execHttp(content);
    }

    public boolean sendRichMessage(String appkey, int tabNo, RichMessageVo message) {
        List<RichMessageItem> richMessageItemList = message.getRichMessageItemList();
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), message.getAccounts());
        JSONObject msg = getMessageJson();
        setTabNo(msg, tabNo);
        msg.put("appid", pushPropEntity.getAppId());
        initRichMsg(richMessageItemList, msg);
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 6);//富文本消息
        content.put("msg", msg);
        return execHttp(content);
    }

    public boolean sendPubaccRichMessage(String appkey, RichMessageVo message) {
        List<RichMessageItem> richMessageItemList = message.getRichMessageItemList();
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), message.getAccounts());
        JSONObject msg = getPubaccMessageJson();
        msg.put("appid", pushPropEntity.getAppId());
        initRichMsg(richMessageItemList, msg);

        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 6);//富文本消息
        content.put("msg", msg);
        return execHttp(content);
    }

    public boolean sendTextMessage(String appkey, int tabNo, TextMessageVo textMessage) {
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), textMessage.getAccounts());
        JSONObject msg = getMessageJson();
        setTabNo(msg, tabNo);
        msg.put("text", textMessage.getMessage());
        msg.put("appid", pushPropEntity.getAppId());
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 2);//文本消息
        content.put("msg", msg);
        return execHttp(content);
    }

    public boolean sendPubaccTextMessage(String appkey, TextMessageVo textMessage) {
        PushPropEntity pushPropEntity = findByAppKey(appkey);
        JSONObject from = getFromJson(pushPropEntity);
        JSONArray tos = getTos(pushPropEntity.getCommunityNo(), textMessage.getAccounts());
        JSONObject msg = getMessageJson();
        msg.put("text", textMessage.getMessage());
        JSONObject content = new JSONObject();
        content.put("from", from);
        content.put("to", tos);
        content.put("type", 2);//文本消息
        content.put("msg", msg);
        execHttp(content);
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

    private JSONObject getPubaccMessageJson() {
        JSONObject msg = new JSONObject();
        msg.put("todo", "0");
        return msg;
    }

    private JSONObject getMessageJson() {
        JSONObject msg = new JSONObject();
        msg.put("todo", "1");
        return msg;
    }

    private void setTabNo(JSONObject msg, int tabNo) {
        switch (tabNo) {
            case 0:
                msg.put("todoPriStatus", "undo");
                break;
            case 1:
                msg.put("todoPriStatus", "done");
                break;
            case 2:
                break;
            default:
                msg.put("todoPriStatus", "undo");
        }
    }

    private void initRichMsg(List<RichMessageItem> richMessageItemList, JSONObject msg) {
        if(richMessageItemList.size() == 1) {
            msg.put("model", 2);
        } else {
            msg.put("model", 3);
        }

        JSONArray msgList = new JSONArray();
        for (RichMessageItem richMsgItem : richMessageItemList) {
            JSONObject msgItem = new JSONObject();
            msgItem.put("data", DateFormat.getInstance().format(new Date()));
            msgItem.put("title", richMsgItem.getTitle());
            msgItem.put("text", richMsgItem.getMessage());
            msgItem.put("name", richMsgItem.getImageName());
            msgItem.put("pic", richMsgItem.getPicBase64());
            msgList.put(msgItem);
        }
        msg.put("list", msgList);
    }

    private boolean execHttp(JSONObject content) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(pushUrl);
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
}
