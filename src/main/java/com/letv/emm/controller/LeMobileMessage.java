package com.letv.emm.controller;

import com.letv.emm.services.MessageService;
import com.letv.emm.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/LeMobile/api/send")
public class LeMobileMessage {
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/text/{tabNo}/{appKey}", method = RequestMethod.POST)
    public StatusVo sendTextMessage(@PathVariable("tabNo") int tabNo, @PathVariable("appKey") String appKey, @RequestBody TextMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendTextMessage(appKey, tabNo, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/pubacc/text/{appKey}", method = RequestMethod.POST)
    public StatusVo sendPubaccTextMessage(@PathVariable("appKey") String appKey, @RequestBody TextMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendPubaccTextMessage(appKey, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/link/{tabNo}/{appKey}", method = RequestMethod.POST)
    public StatusVo sendLinkMessage(@PathVariable("tabNo") int tabNo, @PathVariable("appKey") String appKey, @RequestBody LinkMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendLinkMessage(appKey, tabNo, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/pubacc/link/{appKey}", method = RequestMethod.POST)
    public StatusVo sendPubaccLinkMessage(@PathVariable("appKey") String appKey, @RequestBody LinkMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendPubaccLinkMessage(appKey, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/rich/{tabNo}/{appKey}", method = RequestMethod.POST)
    public StatusVo sendPubaccRichMessage(@PathVariable("tabNo") int tabNo, @PathVariable("appKey") String appKey, @RequestBody RichMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendRichMessage(appKey, tabNo, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    @RequestMapping(value = "/pubacc/rich/{appKey}", method = RequestMethod.POST)
    public StatusVo sendPubaccRichMessage(@PathVariable("appKey") String appKey, @RequestBody RichMessageVo message) {
        StatusVo statusVo = new StatusVo();
        if (null != messageService.findByAppKey(appKey)) {
            boolean status = messageService.sendPubaccRichMessage(appKey, message);
            resetStatusWithHttpResponse(statusVo, status);
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("AppKey is wrong!");
        }

        return statusVo;
    }

    private void resetStatusWithHttpResponse(StatusVo statusVo, boolean status) {
        if (status) {
            statusVo.setCode(StatusCode.SUCCESS.ordinal());
        } else {
            statusVo.setCode(StatusCode.FATAL.ordinal());
            statusVo.setMessage("Service send message Fatal!");
        }
    }
}
