package com.letv.emm.controller;

import com.letv.emm.entity.PushPropEntity;
import com.letv.emm.services.MessageService;
import com.letv.emm.vo.KingdeePushVo;
import com.letv.emm.vo.PushPropResult;
import com.letv.emm.vo.StatusCode;
import com.letv.emm.vo.StatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/LeMobile/maintain/app/message")
public class LeMobileMessageMaintain {
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
}
