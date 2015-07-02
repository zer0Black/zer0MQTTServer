package com.syxy.protocol.mqttImp.process.Impl.dataHandler;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

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
	
}
