//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.upload;

public interface UploadHander {
    String YYYYMMDDHHMMSS_PATTERN = "yyyyMMddHHmmss";

    UploadToken uploadToken(String var1, String var2, Long var3, UploadFileType var4);

    UploadToken uploadToken(String var1, String var2, UploadFileType var3);
}
