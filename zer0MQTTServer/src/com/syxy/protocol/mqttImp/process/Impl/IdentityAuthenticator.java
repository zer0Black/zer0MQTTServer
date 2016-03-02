package com.syxy.protocol.mqttImp.process.Impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.syxy.protocol.mqttImp.process.Impl.dataHandler.DBConnection;
import com.syxy.protocol.mqttImp.process.Interface.IAuthenticator;

/**
 *  身份校验类，该类的校验仅允许数据库中有的用户通过验证
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-6-30
 */
public class IdentityAuthenticator implements IAuthenticator {

	@Override
	public boolean checkValid(String username, String password) {
		//该处连接数据库，到数据库查询是否有该用户，有则通过验证
		int ret=0;
		DruidPooledConnection conn=null;
		PreparedStatement statement = null;
		ResultSet resultSet=null;
		try {
			conn=DBConnection.getInstance().openConnection();
			String sqlString="select * from zer0_user where username=? and password=?";
			statement = conn.prepareStatement(sqlString);
			statement.setString(1, username);
			statement.setString(2, password);
			resultSet=statement.executeQuery();
			while (resultSet.next()) {
				ret=1;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBConnection.getInstance().closeConnection(conn, statement, resultSet);
		}
		if (ret == 1) {
			return true;
		}else {
			return false;
		}
	}

}
