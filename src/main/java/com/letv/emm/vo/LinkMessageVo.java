package com.letv.emm.vo;

import java.util.List;

public class LinkMessageVo {
    private List<String> accounts;
    /**
     * 文本消息
     */
    private String message;
    private String url;

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
