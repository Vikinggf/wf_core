package com.wf.core.sql.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;

/**
 * FastDFS分布式文件系统操作客户端.
 * 
 * @author jijie.chen
 * @date 2017年5月15日
 */
public class FastDFSUtils {
	private static Logger logger = Logger.getLogger(FastDFSUtils.class);
	private static StorageClient1 storageClient;
	private static String domainCdnUri;

	/**
	 * 
	 * @param file
	 *            文件
	 * @param fileName
	 *            文件名
	 * @return 返回Null则为失败
	 */
	public static String uploadFile(File file) {
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
			if(!fileId.startsWith("/")) 
				fileId = "/" + fileId;
			return fileId;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传失败", e);
			throw new CdnException("上传失败", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	public static String uploadFile(MultipartFile file) {
		try {
			String fileId = getStorageClient().upload_file1(file.getBytes(), getFileExt(file.getOriginalFilename()), null);
			if(!fileId.startsWith("/")) 
				fileId = "/" + fileId;
			return fileId;
		} catch (Exception e) {
			logger.error("上传失败", e);
			throw new CdnException("上传失败", e);
		}
	}

	public static String getDomainUri(String relativePath) {
		if (relativePath == null || "".equals(relativePath))
			return "";
		return getDomainUri() + relativePath;
	}

	/**
	 * 获取域名地址
	 * 
	 * @return
	 */
	public static String getDomainUri() {
		if (domainCdnUri == null)
			domainCdnUri = Global.getConfig("fastdfs.dfs.uri");
		return domainCdnUri;
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
			ByteArrayOutputStream pngOut = new ByteArrayOutputStream();
			ImageIO.write(output, "PNG", pngOut);
			String imagePath = getStorageClient().upload_file1(pngOut.toByteArray(), "png", null);
			if(!imagePath.startsWith("/")) 
				imagePath = "/" + imagePath;
			logger.info("saved image:" + imagePath);
			return imagePath;
		} catch (IOException | MyException e) {
			throw new CdnException("图片转换失败", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
	}

	/**
	 * 获取文件后缀名（不带点）.
	 * 
	 * @return 如："jpg" or "".
	 */
	public static String getFileExt(String fileName) {
		if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
			return "";
		} else {
			return fileName.substring(fileName.lastIndexOf(".") + 1); // 不带最后的点
		}
	}

	private static StorageClient1 getStorageClient() {
		if (storageClient == null) {
			String[] szTrackerServers = Global.getConfig("fastdfs.tracker_server").split(";");
			if (szTrackerServers.length == 0)
				throw new RuntimeException("未找到tracker_server的配置");
			InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
			ClientGlobal.setG_secret_key(Global.getConfig("fastdfs.http.secret_key"));
			ClientGlobal.setG_connect_timeout(5 * 1000);
			ClientGlobal.setG_network_timeout(30 * 1000);
			ClientGlobal.setG_charset(Global.getConfig("fastdfs.charset"));
			ClientGlobal.setG_anti_steal_token(Boolean.getBoolean(Global.getConfig("fastdfs.http.anti_steal_token")));
			for (int i = 0; i < szTrackerServers.length; i++) {
				String[] parts = szTrackerServers[i].split("\\:", 2);
				if (parts.length != 2)
					throw new RuntimeException("配置错误： host:port");
				tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
			}
			ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));
			TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
			TrackerServer trackerServer;
			StorageServer storageServer;
			try {
				trackerServer = trackerClient.getConnection();
				storageServer = trackerClient.getStoreStorage(trackerServer);
			} catch (IOException e) {
				throw new RuntimeException("无法连接服务器", e);
			}
			storageClient = new StorageClient1(trackerServer, storageServer);
		}
		return storageClient;
	}

	// /**
	// * 根据组名和远程文件名来删除一个文件
	// * @param groupName
	// * @param fileName
	// * @return 0为成功，非0为失败，具体为错误代码
	// */
	// public static int deleteFile(String groupName, String fileName) {
	// try {
	// int result = getStorageClient().delete_file(groupName == null ? "group1" : groupName, fileName);
	// return result;
	// } catch (Exception ex) {
	// logger.error(ex);
	// return 0;
	// }
	// }
	//
	// /**
	// * 根据fileId来删除一个文件（我们现在用的就是这样的方式，上传文件时直接将fileId保存在了数据库中）
	// * @param fileId
	// * @return 0为成功，非0为失败，具体为错误代码
	// */
	// public static int deleteFile(String fileId) {
	// try {
	// int result = getStorageClient().delete_file1(fileId);
	// return result;
	// } catch (Exception ex) {
	// logger.error(ex);
	// return 0;
	// }
	// }
	//
	// /**
	// * 修改一个已经存在的文件
	// * @param oldFileId
	// * @param file 新文件
	// * @param filePath 新文件路径
	// * @return 返回空则为失败
	// */
	// public static String modifyFile(String oldFileId, File file, String filePath) {
	// String fileId = null;
	// try {
	// // 先上传
	// fileId = uploadFile(file, filePath);
	// if (fileId == null) {
	// return null;
	// }
	// // 再删除
	// int delResult = deleteFile(oldFileId);
	// if (delResult != 0) {
	// return null;
	// }
	// } catch (Exception ex) {
	// logger.error(ex);
	// return null;
	// }
	// return fileId;
	// }
	//
	// /**
	// * 文件下载
	// * @param fileId
	// * @return 返回一个流
	// */
	// public static InputStream downloadFile(String fileId) {
	// try {
	// byte[] bytes = getStorageClient().download_file1(fileId);
	// InputStream inputStream = new ByteArrayInputStream(bytes);
	// return inputStream;
	// } catch (Exception ex) {
	// logger.error(ex);
	// return null;
	// }
	// }
}
