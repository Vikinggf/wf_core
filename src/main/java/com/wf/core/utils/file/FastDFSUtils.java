package com.wf.core.utils.file;

import com.wf.core.log.LogExceptionStackTrace;
import com.wf.core.utils.Global;
import com.wf.core.utils.TraceIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

/**
 * FastDFS分布式文件系统操作客户端.
 *
 * @author jijie.chen
 * @date 2017年5月15日
 */
public class FastDFSUtils {
    private static final Logger logger = LoggerFactory.getLogger(FastDFSUtils.class);
    private static final String FILE_SEPARATOR = "/";
    private static StorageClient1 storageClient;
    private static String domainCdnUri;
    private static String saveDomainCdnUri;

    /**
     * @param file     文件
     * @return 返回Null则为失败
     */
    public static String uploadFile(File file) {
        if(file == null){
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] file_buff = null;
            if (fis != null) {
                int len = fis.available();
                file_buff = new byte[len];
                fis.read(file_buff);
            }
            String fileId = getStorageClient().upload_file1(file_buff, getFileExt(file.getName()), null);
            if (!fileId.startsWith(FILE_SEPARATOR)) {
                fileId = FILE_SEPARATOR + fileId;
            }
            return fileId;
        } catch (Exception e) {
            logger.error("上传失败:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("关闭流异常:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
                }
            }
        }
    }

    public static String uploadFile(MultipartFile file) {
        if(file == null){
            return null;
        }
        try {
            String fileId = getStorageClient().upload_file1(file.getBytes(), getFileExt(file.getOriginalFilename()), null);
            if (!fileId.startsWith(FILE_SEPARATOR)) {
                fileId = FILE_SEPARATOR + fileId;
            }
            return fileId;
        } catch (Exception e) {
            logger.error("上传失败:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            return null;
        }
    }

    public static String getDomainUri(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return StringUtils.EMPTY;
        }
        return getDomainUri() + relativePath;
    }

    /**
     * 获取图片保存时使用的域名地址(WAP站)
     *
     * @return
     */
    public static String getSaveDomainUri() {
        if (saveDomainCdnUri == null) {
            saveDomainCdnUri = Global.getConfig("fastdfs.save.uri");
        }
        return saveDomainCdnUri;
    }

    /**
     * 获取域名地址
     *
     * @return
     */
    public static String getDomainUri() {
        if (domainCdnUri == null) {
            domainCdnUri = Global.getConfig("fastdfs.dfs.uri");
        }
        return domainCdnUri;
    }

    /**
     * 转圆角图片
     * @param url
     * @return
     */
    public static String roundedImage(String url) {
        if(StringUtils.isBlank(url)){
            return null;
        }
        InputStream in = null;
        HttpURLConnection connection = null;
        try {
            if (url.startsWith("https:")) {
                url = url.replaceFirst("https:", "http:");
            }
            logger.info("圆角图片转换 traceId={} url={}",TraceIdUtils.getTraceId(),url);
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            in = connection.getInputStream();
            String path = uploadImage(in);
            logger.info("圆角图片转换结束 traceId={} path={}", TraceIdUtils.getTraceId(), path);
            return path;
        } catch (IOException e) {
            logger.error("图片转换失败:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭流异常:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 转圆角图片
     * @param file
     * @return
     */
    public static String roundedImage(MultipartFile file) {
        if(file == null){
            return null;
        }
        InputStream in = null;
        try {
            in = file.getInputStream();
            return uploadImage(in);
        } catch (IOException e) {
            logger.error("图片转换失败:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("关闭流异常:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
                }
            }
        }
    }

    /**
     * 上传图片
     *
     * @param in
     * @return
     */
    public static String uploadImage(InputStream in) {
        try {
            BufferedImage image = ImageIO.read(in);
            int w = 150;
            int h = 150;
            BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = output.createGraphics();
            output = g2.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
            g2.dispose();
            g2 = output.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRoundRect(0, 0, w, h, w, h);
            g2.setComposite(AlphaComposite.SrcIn);
            g2.drawImage(image, 0, 0, w, h, null);
            g2.dispose();
            ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
            ImageIO.write(output, "PNG", pngOut);
            String imagePath = getStorageClient().upload_file1(pngOut.toByteArray(), "png", null);
            if (!imagePath.startsWith(FILE_SEPARATOR)) {
                imagePath = FILE_SEPARATOR + imagePath;
            }
            logger.info("上传图片结束 traceId={} path={}", TraceIdUtils.getTraceId(), imagePath);
            return imagePath;
        } catch (IOException | MyException e) {
            logger.error("图片转换失败 traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            return null;
        }
    }

    /**
     * 获取文件后缀名（不带点）.
     *
     * @return 如："jpg" or "".
     */
    public static String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return StringUtils.EMPTY;
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
        }
    }

    private static StorageClient1 getStorageClient() {
        if (storageClient == null) {
            String[] szTrackerServers = Global.getConfig("fastdfs.tracker_server").split(";");
            if (szTrackerServers.length == 0) {
                logger.error("未找到tracker_server的配置 :traceId={}", TraceIdUtils.getTraceId());
            }
            InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
            ClientGlobal.setG_secret_key(Global.getConfig("fastdfs.http.secret_key"));
            ClientGlobal.setG_connect_timeout(5 * 1000);
            ClientGlobal.setG_network_timeout(30 * 1000);
            ClientGlobal.setG_charset(Global.getConfig("fastdfs.charset"));
            ClientGlobal.setG_anti_steal_token(Boolean.getBoolean(Global.getConfig("fastdfs.http.anti_steal_token")));
            for (int i = 0; i < szTrackerServers.length; i++) {
                String[] parts = szTrackerServers[i].split("\\:", 2);
                if (parts.length != 2) {
                    logger.error("配置错误=host:port :traceId={}", TraceIdUtils.getTraceId());
                }
                tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
            ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer;
            StorageServer storageServer;
            try {
                trackerServer = trackerClient.getConnection();
                storageServer = trackerClient.getStoreStorage(trackerServer);
                storageClient = new StorageClient1(trackerServer, storageServer);
            } catch (IOException e) {
                logger.error("无法连接服务器:traceId={}, ex={}", TraceIdUtils.getTraceId(), LogExceptionStackTrace.erroStackTrace(e));
            }
        }
        return storageClient;
    }
}
