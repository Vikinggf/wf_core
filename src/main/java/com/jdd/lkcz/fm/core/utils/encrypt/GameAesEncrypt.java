package com.jdd.lkcz.fm.core.utils.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;


public class GameAesEncrypt {
    private static final String AES_ALG = "AES";
    private static final String AES_CBC_PCK_ALG = "AES/ECB/PKCS7Padding";

    public GameAesEncrypt() {
    }

    public static String encryptContent(String content, String encryptType, String encryptKey, String charset) throws Exception {
        if ("AES".equals(encryptType)) {
            return aesEncrypt(content, encryptKey, charset);
        } else {
            throw new Exception("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    public static String decryptContent(String content, String encryptType, String encryptKey, String charset) throws Exception {
        if ("AES".equals(encryptType)) {
            return aesDecrypt(content, encryptKey, charset);
        } else {
            throw new Exception("当前不支持该算法类型：encrypeType=" + encryptType);
        }
    }

    public static String aesEncrypt(String content, String aesKey, String charset) throws Exception {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher e = Cipher.getInstance(AES_CBC_PCK_ALG);
            e.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(), AES_ALG));
            byte[] encryptBytes = e.doFinal(content.getBytes(charset));
            return new String(Base64.encodeBase64(encryptBytes));
        } catch (Exception var6) {
            throw new Exception("AES加密失败：Aescontent = " + content + "; charset = " + charset, var6);
        }
    }

    public static String aesDecrypt(String content, String key, String charset) throws Exception {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher e = Cipher.getInstance(AES_CBC_PCK_ALG);
            e.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), AES_ALG));
            byte[] cleanBytes = e.doFinal(Base64.decodeBase64(content.getBytes()));
            return new String(cleanBytes, charset);
        } catch (Exception var6) {
            throw new Exception("AES解密失败：Aescontent = " + content + "; charset = " + charset, var6);
        }
    }
}
