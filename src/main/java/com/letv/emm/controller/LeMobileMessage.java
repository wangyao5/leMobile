package com.letv.emm.controller;

import com.letv.emm.entity.PushPropEntity;
import com.letv.emm.services.MessageService;
import com.letv.emm.vo.*;
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
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@RestController
@CrossOrigin
@RequestMapping(path = "/LeMobile/message")
public class LeMobileMessage {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ResponseBody
    public StatusVo regist(@RequestBody KingdeePushVo pushVo) {
        StatusVo statusVo = new StatusVo();
        if (messageService.exists(pushVo.getAppid())) {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("Had registed" + pushVo.getAppid());
        } else {
            PushPropEntity pushPropEntity = messageService.save(pushVo.getAppid(), pushVo.getNo(), pushVo.getPub(), pushVo.getPubk());
            if (null != pushPropEntity) {
                statusVo.setCode(StatusCode.SUCCESS.ordinal());
                PushPropResult pushPropResult = new PushPropResult();
                pushPropResult.setAppKey(pushPropEntity.getAppkey());
                statusVo.setBody(pushPropResult);
            }
        }

        return statusVo;
    }

    @RequestMapping(value = "/query/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public StatusVo query(@PathVariable("appId") String appId) {
        StatusVo statusVo = new StatusVo();
        if (messageService.exists(appId)) {
            PushPropEntity pushPropEntity = messageService.findByAppId(appId);
            if (null != pushPropEntity) {
                statusVo.setCode(StatusCode.SUCCESS.ordinal());
                statusVo.setBody(pushPropEntity);
            }
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("NOT Exist " + appId);
        }

        return statusVo;
    }

    @RequestMapping(value = "/update/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public StatusVo update(@PathVariable("appId") String appId, @RequestBody KingdeePushVo pushVo) {
        StatusVo statusVo = new StatusVo();
        if (messageService.exists(appId)) {
            messageService.update(appId, pushVo.getNo(), pushVo.getPub(), pushVo.getPubk());
            PushPropResult pushPropResult = new PushPropResult();
            pushPropResult.setAppKey(messageService.getAppKey(appId, pushVo.getNo(), pushVo.getPub(), pushVo.getPubk()));
            statusVo.setCode(StatusCode.SUCCESS.ordinal());
            statusVo.setBody(pushPropResult);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("FATAL Not Exists appId = " + appId);
        }
        return statusVo;
    }

    @RequestMapping(value = "/delete/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public StatusVo delete(@PathVariable("appId") String appId) {
        StatusVo statusVo = new StatusVo();
        if (messageService.exists(appId)) {
            messageService.deleteByAppId(appId);
            statusVo.setCode(StatusCode.SUCCESS.ordinal());
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("FATAL Not Exists appId = " + appId);
        }
        return statusVo;
    }

    @RequestMapping(value = "/send/{appKey}", method = RequestMethod.POST)
    public StatusVo sendMessage(@PathVariable("appKey") String appKey, @RequestBody MessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            messageService.sendMessage(appKey, message.getAccount());
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/send/{appId}", method = RequestMethod.POST)
    public void push(@PathVariable("appId") String appid) {
        String url = "http://msg.lecommons.com/pubacc/pubsend";
        JSONObject from = new JSONObject();
        String no = "102";// 工作圈号 XT-673abff0-60e8-403f-9353-bc9d87193a92
        String pub = "XT-673abff0-60e8-403f-9353-bc9d87193a92";// 公共号编码
        String pubk = "17304812cba5ef0a0b152c56ce2bcbcd";// 公共号秘钥
        String appId = "10206";// 轻应用ID
        String time = Long.toString(System.currentTimeMillis());
        String nonce = Double.toString(Math.random()).substring(2);
        from.put("no", no);
        from.put("pub", pub);
        from.put("time", time);
        from.put("nonce", nonce);
        String[] data = new String[]{no, pub, pubk, nonce, time};
        Arrays.sort(data);
        from.put("pubtoken", DigestUtils.sha1Hex(StringUtils.join(Arrays.asList(data), null)));
        JSONArray tos = new JSONArray();
        JSONObject to = new JSONObject();
        to.put("no", no);
        to.put("code", 2);
        to.put("user", new String[]{"shengliguo@letv.com"});
        tos.put(to);
        JSONObject msg = new JSONObject();
        msg.put("text", "已办最新数据测试");
        msg.put("url", "http://oa.test.lecommons.com/appweb/message.jsp?id=0");
        msg.put("appid", appid);
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
    }
}
