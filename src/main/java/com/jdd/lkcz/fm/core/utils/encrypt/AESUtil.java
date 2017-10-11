package com.jdd.lkcz.fm.core.utils.encrypt;

import org.apache.commons.codec.binary.Hex;
import org.apache.shiro.crypto.AesCipherService;

import java.io.*;

/**
 * @author chengsheng.liu
 */
public class AESUtil {

    private static AesCipherService aesCipherService;

    /**
     * 生成加密key
     *
     * @return
     * @throws Exception
     */
    public static byte[] getSecretKey() throws Exception {
        return getAesCipherService().generateNewKey().getEncoded();
    }

    /**
     * 加密key byte转化成String
     *
     * @param pwd
     * @return
     */
    public static String getStringSecretKey(byte[] pwd) {
        return Hex.encodeHexString(pwd);
    }

    /**
     * 加密key String转化成byte
     *
     * @param pwd
     * @return
     * @throws Exception
     */
    public static byte[] getByteSecretKey(String pwd) throws Exception {
        return Hex.decodeHex(pwd.toCharArray());
    }

    /**
     * 数据加密
     *
     * @param data
     * @param pwd
     * @return
     */
    public static String encrypt(String data, byte[] pwd) {
        return getAesCipherService().encrypt(data.getBytes(), pwd).toHex();
    }

    /**
     * 数据解密
     *
     * @param data
     * @param pwd
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, byte[] pwd) throws Exception {
        return new String(getAesCipherService().decrypt(Hex.decodeHex(data.toCharArray()), pwd).getBytes());
    }

    public static AesCipherService getAesCipherService() {
        if (aesCipherService == null) {
            aesCipherService = new AesCipherService();
        }
        return aesCipherService;
    }

    /**
     * 获取远程的文件内容
     *
     * @param path
     * @return
     */
    public static String getSecurityToString(String filePath) {
        BufferedReader br = null;
        String source = "";
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            while ((line = br.readLine()) != null) {
                source += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return source;
    }

    /**
     * 创建文件
     */
    public static void writeToSecurityFile(String path, String content) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(path));
            pw.print(content);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }
}