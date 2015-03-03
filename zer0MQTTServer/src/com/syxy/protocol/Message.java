package com.syxy.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import sun.awt.image.BytePackedRaster;

/**
 * <li>定义MQTT协议固定头部，并作为细分的message的基类
 * <li>作者 zer0
 * <li>创建日期 2015-3-2
 */
public abstract class Message {

	/**
	 * <li>方法名 encodeHeader
	 * <li>返回类型 byte[]
	 * <li>说明 对MQTT协议进行编码
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract byte[] encode() throws IOException;

	/**
	 * <li>方法名 decode
	 * <li>@param buffer
	 * <li>返回类型 HeaderMessage
	 * <li>说明 对MQTT协议头部进行解码，并返回整个类
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-2
	 */
	public abstract Message decode(ByteBuffer byteBuffer) throws IOException;

	/**
	 * <li>方法名 handlerMessage
	 * <li>返回类型 void
	 * <li>说明 根据协议，对解码后的信息做相应的处理
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-3
	 */
	public abstract void handlerMessage();

	/**
	 * <li>方法名 messageLength
	 * <li>返回类型 int
	 * <li>说明 计算整个协议的字节数(可变协议头+消息体)
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-3
	 */
	public abstract int messageLength();
	
}
