package com.wf.core.encrypt;

import org.junit.Test;

import java.util.Base64;

/**
 * RSA测试类
 * @author fxy
 * @date 2018/1/24
 */
public class RSATest {


    public static void main(String[] args) throws Exception {
        String filepath = "E:/tmp/";

        RSAEncrypt.genKeyPair(filepath);

        System.out.println("--------------公钥加密私钥解密过程-------------------");
        String plainText = "ihep_公钥加密私钥解密";
        //公钥加密过程
        byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)), plainText.getBytes());
        String cipher = Base64.getEncoder().encodeToString(cipherData);
        //私钥解密过程
        byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.getDecoder().decode(cipher));
        String restr = new String(res);
        System.out.println("原文：" + plainText);
        System.out.println("加密：" + cipher);
        System.out.println("解密：" + restr);
        System.out.println();

        System.out.println("--------------私钥加密公钥解密过程-------------------");
        plainText = "ihep_私钥加密公钥解密";
        //私钥加密过程
        cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), plainText.getBytes());
        cipher = Base64.getEncoder().encodeToString(cipherData);
        //公钥解密过程
        res = RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)), Base64.getDecoder().decode(cipher));
        restr = new String(res);
        System.out.println("原文：" + plainText);
        System.out.println("加密：" + cipher);
        System.out.println("解密：" + restr);
        System.out.println();

        System.out.println("---------------私钥签名过程------------------");
        String content = "ihep_这是用于签名的原始数据";
        String signstr = RSASignature.sign(content, RSAEncrypt.loadPrivateKeyByFile(filepath));
        System.out.println("签名原串：" + content);
        System.out.println("签名串：" + signstr);
        System.out.println();

        System.out.println("---------------公钥校验签名------------------");
        System.out.println("签名原串：" + content);
        System.out.println("签名串：" + signstr);

        System.out.println("验签结果：" + RSASignature.doCheck(content, signstr, RSAEncrypt.loadPublicKeyByFile(filepath)));
        System.out.println();

    }


    @Test
    public void test() throws Exception {
        //公钥
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEbg4I0KEFb7wBIM9+Lh4kYLTjxhhT+yi0omMTwThd1vfTRKzo3zEL0AZQZ1ugDLaeai0NE5RKRizUFNQdwYd1ecMgQvPA/POCyDAzDccK35wTEYQb12KOCScHOhR6+/McLVvYwV31U48Zv349Q9lVTqTxLNSKvy5ZS2nkHz4wdQIDAQAB";
        //私钥
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIRuDgjQoQVvvAEgz34uHiRgtOPGGFP7KLSiYxPBOF3W99NErOjfMQvQBlBnW6AMtp5qLQ0TlEpGLNQU1B3Bh3V5wyBC88D884LIMDMNxwrfnBMRhBvXYo4JJwc6FHr78xwtW9jBXfVTjxm/fj1D2VVOpPEs1Iq/LllLaeQfPjB1AgMBAAECgYAwHdyzjzoyQ3rH3igF7LO++/Q9nkhQmz0bBLWDA/16+xRoitdYdV/YrujcZfGAoZ3oySM9URPcOMso87huZ1bT7nCcqXh88drjDJwlZOWYhtP+b8LGhonoE+DGDhEQoAlVs3iDLkCbGt/5ueifD7RTCYgWQ3NH4CECs4yC+JwzAQJBALjJr9fNpzFLWQpmCntuqbSjwn9Y+e1tipn/2JOCcLfjbOw0cXKG4ztG5ZaZ/LfHdRyrtFcDdwjnNFiaN8zp+oUCQQC3dvng4U/pasmMQh3lRFMZ5Fj1lGFqfW97nWvQbCADiO+UQYVseMrX9Wpd7scPBa0hTDGSwfJvAvQMJhVH+VkxAkBiDHJHWZG7MDXAtMAfGmrltHbBR57lglgmuoBaVL4mdIAZHQKNOFFd5JJxxKc5hJX5boo0GVyR1swJr1qoWgZZAkAx9gVwNwti/kCqoGqxv5x9xZBwOBsbO9N7L1Bn7rn9HHJZxkoGUxKyItP3honQznPqSGyLkaNqlbxN/qT297FRAkAlYdVeMwiptNc3L0DNqgZpKF8t7WF5iPNA1sPdb7giCuz3RS2ia00vKNaB0CBSfUXB8YEl9sIRsxQZqZ05EkXG";

        System.out.println("--------------公钥加密私钥解密过程-------------------");
        String plainText = "ihep_公钥加密私钥解密原文";
        //公钥加密过程
        byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(publicKey), plainText.getBytes());
        String cipher = Base64.getEncoder().encodeToString(cipherData);
        //私钥解密过程
        byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(privateKey), Base64.getDecoder().decode(cipher));
        String restr = new String(res);
        System.out.println("原文：" + plainText);
        System.out.println("加密：" + cipher);
        System.out.println("解密：" + restr);
        System.out.println();

    }


}