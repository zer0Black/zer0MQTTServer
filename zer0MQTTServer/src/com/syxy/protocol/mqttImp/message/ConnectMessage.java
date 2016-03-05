package com.syxy.protocol.mqttImp.message;

/**
 * MQTT协议Connect消息类型实现类，客户端请求服务器连接的消息类型
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-2
 */
public class ConnectMessage extends Message {
	
	public ConnectMessage(FixedHeader fixedHeader, ConnectVariableHeader variableHeader,
			ConnectPayload payload) {
		super(fixedHeader, variableHeader, payload);
	}
	
	@Override
	public ConnectVariableHeader getVariableHeader() {
		return (ConnectVariableHeader)super.getVariableHeader();
	}
	
	@Override
	public ConnectPayload getPayload() {
		return (ConnectPayload)super.getPayload();
	}

//	@Override
//	public Message decode(ByteBuffer byteBuffer, int messageLength) throws IOException {
//		//将byteBuffer转换成DataInputStream，方便调用readUTF方法来读取UTF数据
//		InputStream in = new ByteArrayInputStream(byteBuffer.array());
//		DataInputStream dataInputStream = new DataInputStream(in);
//		
//		ConnectMessage connectMessage = new ConnectMessage();
//		
//		connectMessage.setProtocolName(dataInputStream.readUTF());
//		connectMessage.setProtocolVersionNumber(dataInputStream.readByte());
//		//读取Connect Flags的各个参数
//		byte connectFlags = dataInputStream.readByte();
//		connectMessage.setHasUsername((connectFlags & 0x80) > 0);//0x80=10000000
//		connectMessage.setHasPassword((connectFlags & 0x40) > 0);//0x40=01000000
//		connectMessage.setWillRetain((connectFlags & 0x20) > 0);//0x20=00100000
//		connectMessage.setWillQoS(QoS.valueOf(connectFlags >> 3 & 0x03));//0x03=00000011
//		connectMessage.setHasWill((connectFlags & 0x04) > 0);//0x04=00000100
//		connectMessage.setCleanSession((connectFlags & 0x02) > 0);//0x02=00000010
//		connectMessage.setReservedIsZero((connectFlags & 0x01) == 0 );//0x00=0000001
//		
//		connectMessage.setKeepAlive(dataInputStream.readShort());//读取心跳包
//		connectMessage.setClientId(dataInputStream.readUTF());//读取客户端ID
//		
//		//如果willFlag的值为1，willTopic和WillMessage便有内容
//		if (connectMessage.isHasWill()) {
//			connectMessage.setWillTopic(dataInputStream.readUTF());
//			connectMessage.setWillMessage(dataInputStream.readUTF());
//		}
//		
//		if (connectMessage.isHasUsername()) {
//			connectMessage.setUsername(dataInputStream.readUTF());
//		}
//		
//		if (connectMessage.isHasPassword()) {
//			connectMessage.setPassword(dataInputStream.readUTF());
//		}
//		
//		
//		//必须再把协议头的解码对象添加进来一起返回
//		connectMessage.setHeaderMessage(this.getHeaderMessage());
//		
//		byteBuffer.position(messageLength);
//		BufferPool.removeReadedData(byteBuffer);
//		
//		return connectMessage;
//	}

}
