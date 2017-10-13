package com.wf.core.idg.util;

import java.util.Arrays;

/**
 * byte数组工具
 * @author Fe
 * @version 
 * 2015-01-02 <strong>1.1</strong> 增加 {@link #getArray()} 方法
 * 2013-04-02 <strong><s>1.0</s></strong>
 */
public class ByteArray {
	protected byte[] data;
	protected int size;

	public ByteArray(){
		this(64);
	}

	public ByteArray(int size){
		data = new byte[size];
	}

	/**
	 * 简单的清理。仅将size年重置为0，而不清理占用的内存。
	 */
	public void simpleClear() {
		size = 0;
	}

	/**
	 * 清理。所有的数据都重置为0。
	 */
	public void clear() {
		for (int i = 0; i < size; i++)
			data[i] = 0;
		size = 0;
	}
	public void add(byte e){
		ensureCapacity(size + 1);
		data[size++] = e;
	}
	
	public void noCheckAdd(byte e) throws IndexOutOfBoundsException {
		data[size++] = e;
	}
	
	public boolean addAll(byte[] es){
		int numNew = es.length;
		ensureCapacity(size + numNew);
		System.arraycopy(es, 0, data, size, numNew);
		size += numNew;
		return numNew != 0;
	}
	
	public boolean addAll(byte[] es, int start, int length){
		ensureCapacity(size + length);
		System.arraycopy(es, start, data, size, length);
		size += length;
		return length != 0;
	}
	
	/**
	 * 不安全的get。它不验证index的合法性。
	 */
	public byte get(int index) {
		return data[index];
	}
	
	/**
	 * 与 {@link #array()} 方法不同，它可能返回当前存储数据本身。
	 */
	public byte[] getArray() {
		if (data.length == size)
			return data;
		else
			return array();
	}

	/**
	 * 始终反回一个新的数组
	 */
	public byte[] array() {
		byte[] bs = new byte[size];
		System.arraycopy(data, 0, bs, 0, size);
		return bs;
	}

	public int size(){
		return size;
	}
	
	/**
	 * 移除最后一个元素
	 */
	public byte removeLast() {
		byte result = data[--size];
		data[size] = 0;
		return result;
	}

	protected void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			byte[] bs = new byte[newCapacity];
			System.arraycopy(data, 0, bs, 0, size);
			data = bs;
		}
	}

	@Override
	public String toString() {
		return "ByteArray [data=" + Arrays.toString(data) + "]";
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ByteArray)
			return Arrays.equals(data, ((ByteArray) obj).data);
		return false;
	}
}
