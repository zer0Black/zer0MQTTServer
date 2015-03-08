package com.syxy.protocol.mqttImp.process;

import java.io.IOException;

/**
 * <li>说明 身份验证类
 * <li>作者 zer0
 * <li>创建日期 2015-3-8
 */
public interface IAuthenticator {

	/**
	 * <li>方法名 checkValid
	 * <li>@param username
	 * <li>@param password
	 * <li>返回类型 boolean
	 * <li>说明 校验用户名和密码是否正确
	 * <li>作者 zer0
	 * <li>创建日期 2015-3-8
	 */
	boolean checkValid(String username, String password);
	
}
