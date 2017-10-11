//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.upload.qiniu;

import com.wf.core.upload.UploadFileType;
import com.wf.core.upload.UploadHander;
import com.wf.core.upload.UploadToken;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

public class QiniuUploadHanderImpl implements UploadHander, InitializingBean {
    public static final String YYYYMMDDHHMMSS_PATTERN = "yyyyMMddHHmmssSSS";
    private Random random = new Random();
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String accessUrl;
    private Long maxSize;
    private Auth auth;

    public QiniuUploadHanderImpl() {
    }

    public UploadToken uploadToken(String webModule, String module, Long maxSize, UploadFileType type) {
        StringMap map = new StringMap();
        map.put("fsizeLimit", maxSize);
        String key = this.createKey(webModule, module);
        String uptoken = this.auth.uploadToken(this.bucketName, (String)null, 3600L, map);
        UploadToken token = new UploadToken();
        token.setUptoken(uptoken);
        token.setKey(key);
        token.setAccessUrl(this.accessUrl);
        return token;
    }

    private String createKey(String webModule, String module) {
        DateFormat defaultFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = defaultFormat.format(new Date());
        String rand = Integer.toString(this.random.nextInt(10000) + 10000).substring(1);
        return webModule + "/" + module + "/" + time + "/" + rand;
    }

    public UploadToken uploadToken(String webModule, String module, UploadFileType type) {
        return this.uploadToken(webModule, module, this.maxSize, type);
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    public void afterPropertiesSet() throws Exception {
        this.auth = Auth.create(this.accessKey, this.secretKey);
    }

    public String getAccessUrl() {
        return this.accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }
}
