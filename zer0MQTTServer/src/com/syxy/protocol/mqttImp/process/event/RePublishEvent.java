package com.syxy.protocol.mqttImp.process.event;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.syxy.protocol.mqttImp.process.ProtocolProcess;
import com.syxy.protocol.mqttImp.process.event.job.RePublishJob;

/**
 *  Publish消息重发事件，在消息Qos=1 or 2 的时间，若在一段时间后未收到响应包，则需要重发该Publish消息
 * 
 * @author zer0
 * @version 1.0
 * @date 2015-11-26
 */
public class RePublishEvent {
	
	private final static Logger Log = Logger.getLogger(RePublishEvent.class);
	private ProtocolProcess protocolProcess;
	
	public RePublishEvent(ProtocolProcess process) {
		protocolProcess = process;
	}
	
	public Scheduler run() throws Exception {
		SchedulerFactory sf = new StdSchedulerFactory();
	    Scheduler sched = sf.getScheduler();
	    
	    JobDetail job = newJob(RePublishJob.class).withIdentity("rePublishJob", "group").build();
	    job.getJobDataMap().put("ProtocolProcess", protocolProcess);//给job传递参数
	    
	    SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("rePublishTrigger", "group")
	            .startAt(futureDate(10, IntervalUnit.SECOND)).build();
	    Date ft = sched.scheduleJob(job, trigger);
	    sched.start();
	    Log.info("publish重发事件于{" + ft + "}执行");
		return sched;
	}
	
}
