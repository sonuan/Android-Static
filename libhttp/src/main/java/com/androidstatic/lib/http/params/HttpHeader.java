package com.androidstatic.lib.http.params;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * 网络请求头部
 * 
 * @author kycq
 *
 */
public class HttpHeader {
	/** 请求头部集 */
	private HashMap<String, String> mHeaders;

	/**
	 * 构造方法
	 */
	public HttpHeader() {
		mHeaders = new HashMap<String, String>();
	}

	/**
	 * 添加头部
	 * 
	 * @param name
	 *            键
	 * @param value
	 *            值
	 */
	public void addHeader(String name, String value) {
		mHeaders.put(name, value);
	}

	/**
	 * 添加头部
	 * 
	 * @param header
	 *            头部集
	 */
	public void addHeader(HttpHeader header) {
		if (header != null) {
			mHeaders.putAll(header.mHeaders);
		}
	}

	/**
	 * 获取头部
	 * 
	 * @param name
	 *            键
	 * @return 值
	 */
	public String getHeader(String name) {
		return mHeaders.get(name);
	}

	/**
	 * 删除头部
	 * 
	 * @param name
	 *            键
	 */
	public void removeHeader(String name) {
		mHeaders.remove(name);
	}

	/**
	 * 清空头部
	 */
	public void clear() {
		mHeaders.clear();
	}

	/**
	 * 头部键集合
	 * 
	 * @return 键集合
	 */
	public Set<String> nameSet() {
		return mHeaders.keySet();
	}

	/**
	 * 头部值集合
	 * 
	 * @return 值集合
	 */
	public Collection<String> valueSet() {
		return mHeaders.values();
	}

	public HashMap<String, String> getHeaders() {
		return mHeaders;
	}
}
