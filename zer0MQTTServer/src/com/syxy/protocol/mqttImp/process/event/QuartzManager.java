package com.syxy.protocol.mqttImp.process.event;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.syxy.protocol.mqttImp.process.event.job.RePublishJob;

public class QuartzManager {

	private final static Logger Log = Logger.getLogger(QuartzManager.class);
	
	private static SchedulerFactory sf = new StdSchedulerFactory();
	
	/**
	 * 添加调度任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @param jobClass
	 * @param time
	 * @param jobParam
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	@SuppressWarnings("unchecked")
	public static void addJob(String jobName, String jobGroupName,  
            String triggerName, String triggerGroupName, Class jobClass,  
            int time, Map<String, Object> jobParam) {  
        try {  
            Scheduler sched = sf.getScheduler();  
            JobDetail job = newJob(jobClass).withIdentity(jobName, jobGroupName).build();// 任务名，任务组，任务执行类  
            if (jobParam != null && jobParam.size() > 0) {
				//给job添加参数
			}
            // 触发器  
            SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity(triggerName, triggerGroupName)
    	            .startAt(futureDate(time, IntervalUnit.SECOND)).build();
            Date ft = sched.scheduleJob(job, trigger);
            Log.info(jobName + "启动于" + ft);
            // 启动  
            if (!sched.isShutdown()) {  
                sched.start();  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }
	
	/**
	 * 删除调度任务
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @author zer0
	 * @version 1.0
	 * @date 2015-11-28
	 */
	public static void removeJob(String jobName,String jobGroupName,  
            String triggerName,String triggerGroupName){
		try {
			Scheduler sched = sf.getScheduler();
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroupName);
			JobKey jobKey = new JobKey(jobName, jobGroupName);
			sched.pauseTrigger(triggerKey);//停止触发器  
			sched.unscheduleJob(triggerKey);//移除触发器  
			sched.deleteJob(jobKey);//删除任务
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}
 
	
}
