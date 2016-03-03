package com.syxy.protocol.mqttImp.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.syxy.protocol.mqttImp.QoS;
import com.syxy.protocol.mqttImp.Type;

/**
 * MQTT协议SubAck消息类型实现类，对Subscribe包的确认
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-5
 */
public class SubAckMessage extends Message {

	private List<QoS> grantedQoSs = new ArrayList<QoS>();
	
	public SubAckMessage(){
		super(Type.SUBACK);
	}
	
	public SubAckMessage(int packageID){
		super(Type.SUBACK);
		this.setPackgeID(packageID);
	}
	
	public SubAckMessage(HeaderMessage headerMessage){
		super(headerMessage);
	}
	
	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);
		
		dos.write(this.encodePackageID());//写入包ID
		//写入returnCode
		if(grantedQoSs!=null){
			for(QoS qos:grantedQoSs){
				dos.write(qos.val);
			}
		}
		//将dos转换为byte[]
		byte[] bArray = byteOut.toByteArray();	
		return bArray;
	}

	@Override
	public Message decode(ByteBuffer byteBuffer, int messageLength)
			throws IOException {
		throw new UnsupportedOperationException("SUBACK无需解码，该类型仅能从服务器发送到客户端");
	}

	@Override
	public int messageLength(Message msg) {
		return grantedQoSs == null ? 2 : 2 + grantedQoSs.size();
	}

	@Override
	public boolean isMessageIdRequired() {
		return true;
	}
	
	public List<QoS> getGrantedQoSs() {
		return grantedQoSs;
	}

	public void setGrantedQoSs(List<QoS> grantedQoSs) {
		this.grantedQoSs = grantedQoSs;
	}
	
	public void addGrantedQoSs(QoS qos){
		grantedQoSs.add(qos);
	}
}
