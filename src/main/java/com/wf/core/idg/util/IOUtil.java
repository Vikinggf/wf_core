package com.wf.core.idg.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class IOUtil {
	/**
	 * 读取流
	 */
	public static String readStream(InputStream stream) throws IOUtilException {
		return readStream(stream, null);
	}

	/**
	 * 读取流
	 */
	public static String readStream(InputStream stream, String charset) throws IOUtilException {
		byte[] data = readStreamByte(stream);
		if (charset == null)
			return new String(data);
		else {
			try {
				return new String(data, charset);
			} catch (UnsupportedEncodingException e) {
				throw new IOUtilException(charset + "编码无法识别", e);
			}
		}
	}

	/**
	 * 读取流
	 */
	public static byte[] readStreamByte(InputStream stream) throws IOUtilException {
		ByteArray array = new ByteArray();
		try {
			byte[] buf = new byte[65536];
			int i;
			while ((i = stream.read(buf)) != -1) {
				array.addAll(buf, 0, i);
			}
		}catch (IOException e) {
			throw new IOUtilException("流读取错误", e);
		} finally {
			try {
				if(stream != null)
					stream.close();
			} catch (IOException e) {}
		}
		return array.array();
	}
}
