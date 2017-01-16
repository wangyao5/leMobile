package com.letv.emm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "t_push_prop")
@Entity
public class PushPropEntity {
    private String appId;//轻应用ID

    private String communityNo;// 工作圈号 102

    private String pub;// 公共号编码

    private String pubk;// 公共号秘钥

    private String appkey;//为第三方调用分配的key

    @Id
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "community_no", nullable = false)
    public String getCommunityNo() {
        return communityNo;
    }

    public void setCommunityNo(String communityNo) {
        this.communityNo = communityNo;
    }

    @Column(name = "pub", nullable = false)
    public String getPub() {
        return pub;
    }


    public void setPub(String pub) {
        this.pub = pub;
    }

    @Column(name = "pub_key", nullable = false)
    public String getPubk() {
        return pubk;
    }

    public void setPubk(String pubk) {
        this.pubk = pubk;
    }

    @Column(name = "appkey", nullable = false)
    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
