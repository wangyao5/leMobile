package com.letv.emm.vo;

import java.util.List;

public class TextMessageVo {
    /**
     * 域账号列表
     */
    private List<String> accounts;

    /**
     * 文本消息
     */
    private String message;

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
}
