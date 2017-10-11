//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.upload;

import java.io.Serializable;

public class UploadToken implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uptoken;
    private String key;
    private String accessUrl;

    public UploadToken() {
    }

    public String getUptoken() {
        return this.uptoken;
    }

    public void setUptoken(String uptoken) {
        this.uptoken = uptoken;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAccessUrl() {
        return this.accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }
}
