package com.syxy.protocol.mqttImp.process.Impl;

import com.syxy.protocol.mqttImp.process.Interface.IAuthenticator;

/**
 * <li>说明 身份校验类，该类的校验仅允许数据库中有的用户通过验证
 * <li>作者 zer0
 * <li>创建日期 2015-6-30
 */
public class IdentityAuthenticator implements IAuthenticator {

	@Override
	public boolean checkValid(String username, String password) {
		// TODO Auto-generated method stub
		//该处连接数据库，到数据库查询是否有该用户，有则通过验证
		return true;
	}

}
