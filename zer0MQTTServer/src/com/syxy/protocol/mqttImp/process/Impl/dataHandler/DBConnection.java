package com.syxy.protocol.mqttImp.process.Impl.dataHandler;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 *  数据库连接类，处理了连接池和数据库打开关闭操作
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-6-30
 */
public class DBConnection {

	private static DruidDataSource  ds = null;   
	private static DBConnection dbConnection = null;
	private static final String CONFIG_FILE = System.getProperty("user.dir") + "/zer0MQTTServer/resource/druid.properties";
		
	static {
		try{
	        FileInputStream in = new FileInputStream(CONFIG_FILE);
	        Properties props = new Properties();
	        props.load(in);
	        ds = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
	     }catch(Exception ex){
	         ex.printStackTrace();
	     }
	 }
	
	private DBConnection() {}
	
	 public static synchronized DBConnection getInstance() {
	        if (null == dbConnection) {
	        	dbConnection = new DBConnection();
	        }
	        return dbConnection;
	    }
	     
	public DruidPooledConnection  openConnection() throws SQLException {
			return ds.getConnection();
	}   

	/**
	 * 关闭数据库连接
	 * @param connection
	 * @param statement
	 * @param resultSet
	 * @author zer0
	 * @version 1.0
	 * @date 2015-7-7
	 */
	public void closeConnection(DruidPooledConnection connection,Statement statement,ResultSet resultSet) {
		if (resultSet!=null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (connection!=null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
