package com.letv.emm.vo;

import java.util.List;

public class RichMessageVo {
    private List<String> accounts;
    private List<RichMessageItem> richMessageItemList;

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts) {
        this.accounts = accounts;
    }

    public List<RichMessageItem> getRichMessageItemList() {
        return richMessageItemList;
    }

    public void setRichMessageItemList(List<RichMessageItem> richMessageItemList) {
        this.richMessageItemList = richMessageItemList;
    }
}
