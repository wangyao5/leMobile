package com.letv.emm.entity;

import javax.persistence.Table;

@Table(name = "t_push_prop")
public class PushPropEntity {
    private String pub;

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }
}
