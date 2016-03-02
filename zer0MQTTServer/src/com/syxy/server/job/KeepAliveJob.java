package com.syxy.server.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.syxy.protocol.mqttImp.process.ProtocolProcess;
import com.syxy.server.ClientSession;
import com.syxy.util.QuartzManager;

/**
 *  Publish消息重发事件需要做的工作，即重发消息到对应的clientID
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-11-26
 */
public class KeepAliveJob implements Job{
	
	private final static Logger Log = Logger.getLogger(KeepAliveJob.class);
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		//取出参数，参数为ProtocolProcess，调用此类的函数
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		ClientSession clientSession = (ClientSession) dataMap.get("ClientSession");
		String clientID = (String) dataMap.get("clientID");
		
		Log.info("规定时间内未收到心跳包,应断开连接");
		QuartzManager.removeJob(clientID, "keepAlives", clientID, "keepAlives");
		clientSession.close();
	}
	
}
