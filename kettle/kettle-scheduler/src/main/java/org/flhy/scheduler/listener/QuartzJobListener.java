package org.flhy.scheduler.listener;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class QuartzJobListener extends JdbcDaoSupport implements JobListener {
	
	public String getName() {
		return getClass().getName();
	}
	
	public void jobExecutionVetoed(JobExecutionContext context) {
		
	}
	
	public void jobToBeExecuted(JobExecutionContext context) {
		getJdbcTemplate().update("insert into kettle_execution_log(fireId, jobName, startTime, executionConfiguration) values(?,?,?,?)",
				context.getFireInstanceId(),
				context.getJobDetail().getKey().getName(),
				context.getFireTime(),
				context.getMergedJobDataMap().getString("executionConfiguration"));
	}
	
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jee) {
		try {
			String status = "成功";
			if(jee != null || context.getMergedJobDataMap().getLong("error") > 0)
				status = "失败";
			getJdbcTemplate().update("update kettle_execution_log set endTime=?, execMethod=?, status=?, executionLog=? where fireId=?",
					new Date(),
					context.getMergedJobDataMap().getString("execMethod"),
					status,
					context.getMergedJobDataMap().getString("executionLog"),
					context.getFireInstanceId()
					);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
