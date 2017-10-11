package com.jdd.lkcz.fm.core.utils.file;

import com.jdd.lkcz.game.common.exception.CdnException;
import com.jdd.lkcz.game.common.utils.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * cdn utils
 *
 * @author Fe 2016年12月6日
 */
public class CdnUtils {
    private static Logger logger = LoggerFactory.getLogger(CdnUtils.class);

    /**
     * CDN路径
     */
    private static String cdnUri;

    /**
     * domain.CDN路径
     */
    private static String domainCdnUri;

    /**
     * CDN存储路径
     */
    private static String cdnSavePath;

    /**
     * 相对路径
     *
     * @param relativePath
     * @return
     */
    public static String getUri(String relativePath) {
        if (relativePath == null || "".equals(relativePath))
            return "";
        return getUri() + relativePath;
    }

    /**
     * 获取cdn的路径
     *
     * @return
     */
    public static String getUri() {
        if (cdnUri == null) {
            cdnUri = Global.getConfig("cdn.uri");
            if (cdnUri.endsWith("/"))
                cdnUri = cdnUri.substring(0, cdnUri.length() - 1);
        }
        return cdnUri;
    }

    /**
     * 获取域名地址
     *
     * @return
     */
    public static String getDomainUri() {
        if (domainCdnUri == null) {
            domainCdnUri = Global.getConfig("domain.cdn.uri");
            if (domainCdnUri.endsWith("/"))
                domainCdnUri = domainCdnUri.substring(0, domainCdnUri.length() - 1);
        }
        return domainCdnUri;
    }

    /**
     * 相对路径
     *
     * @param relativePath
     * @return
     */
    public static String getDomainUri(String relativePath) {
        if (relativePath == null || "".equals(relativePath))
            return "";
        return getDomainUri() + relativePath;
    }


    /**
     * 获取cdn的存储路径
     *
     * @return
     */
    public static String getSavePath() {
        if (cdnSavePath == null) {
            cdnSavePath = Global.getConfig("cdn.save.path");
            if (cdnSavePath.endsWith("/"))
                cdnSavePath = cdnSavePath.substring(0, cdnSavePath.length() - 1);
        }
        return cdnSavePath;
    }

    /**
     * 保存从客户端上传的图片
     *
     * @param file
     */
    public static String saveFile(MultipartFile file) {
        String extName = FileUtils.getFileExtension(file.getOriginalFilename());
        String filePath = createPath() + (extName == null ? "" : ("." + extName));
        FileUtils.saveFile(file, getSavePath() + filePath);
        logger.info("保存附件：" + filePath);
        return filePath;
    }

    /**
     * 保存从客户端上传的图片
     *
     * @param file
     */
    public static String saveFile(File file) {
        String extName = FileUtils.getFileExtension(file.getPath());
        String filePath = createPath() + (extName == null ? "" : ("." + extName));
        FileUtils.saveFile(file, getSavePath() + filePath);
        logger.info("保存附件：" + filePath);
        return filePath;
    }

    /**
     * 转圆角图片
     *
     * @param url
     * @return
     */
    public static String roundedImage(String url) {
        InputStream in = null;
        try {
            if (url.startsWith("https:")) {
                url = url.replaceFirst("https:", "http:");
            }
            in = new URL(url).openStream();
            BufferedImage image = ImageIO.read(in);
            int w = image.getWidth();
            int h = image.getHeight();
            w = 150;
            h = 150;
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
            String filePath = createPath() + ".png";
            File file = new File(getSavePath() + filePath);
            if (file.exists())
                file.delete();
            file.getParentFile().mkdirs();
            file.createNewFile();
            ImageIO.write(output, "PNG", file);
            logger.info("saved image:" + file.getAbsolutePath());
            return filePath;
        } catch (IOException e) {
            throw new CdnException("图片转换失败", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 创建一个路径
     *
     * @return
     */
    public static String createPath() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuilder sb = new StringBuilder("/");
        sb.append(uuid.substring(0, 4)).append('/');
        sb.append(uuid.substring(4, 8)).append('/');
        sb.append(uuid.substring(8, 12)).append('/');
        sb.append(uuid.substring(12, 16)).append('/');
        sb.append(uuid.substring(16, 20)).append('/');
        sb.append(uuid.substring(20, 24)).append('/');
        sb.append(uuid.substring(24, 28)).append('/');
        sb.append(uuid.substring(28, 32)).append('/');
        sb.append(uuid);
        return sb.toString();
    }
}
