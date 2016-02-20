package com.syxy.protocol.mqttImp.process.Interface;

/**
 *  身份验证接口
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-3-8
 */
public interface IAuthenticator {

	/**
	 * 校验用户名和密码是否正确
	 * @param username
	 * @param password
	 * @return boolean
	 * @author zer0
	 * @version 1.0
	 * @date 2015-3-8
	 */
	boolean checkValid(String username, String password);
	
}
