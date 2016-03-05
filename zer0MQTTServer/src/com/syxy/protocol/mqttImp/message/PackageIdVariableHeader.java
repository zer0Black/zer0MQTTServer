package com.syxy.protocol.mqttImp.message;

/**
 * MQTT协议中，有部分消息类型的可变头部只含有包ID，把这部分抽取出来，单独成为一个可变头部
 * 
 * @author zer0
 * @version 1.0
 * @date 2016-3-4
 */	
public class PackageIdVariableHeader {
	
	private int packageID;

	public PackageIdVariableHeader(int packageID) {
		if (packageID < 1 || packageID > 65535) {
			throw new IllegalArgumentException("消息ID:" + packageID + "必须在1~65535范围内");
		}
		this.packageID = packageID;
	}

	public int getPackageID() {
		return packageID;
	}

	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}
	
}
