package org.flhy.scheduler.controller;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flhy.ext.App;
import org.flhy.ext.PluginFactory;
import org.flhy.ext.base.GraphCodec;
import org.flhy.ext.utils.JSONArray;
import org.flhy.ext.utils.JSONObject;
import org.flhy.ext.utils.JsonUtils;
import org.flhy.ext.utils.StringEscapeHelper;
import org.flhy.scheduler.dao.ExecutionLogDao;
import org.flhy.scheduler.runner.JobRunner;
import org.flhy.scheduler.runner.TransRunner;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransMeta;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.util.mxUtils;

@Controller
@RequestMapping(value="/schedule")
public class SchedulerController {

	@Autowired
	private Scheduler scheduler;
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/scheduleJob")
	protected void scheduleJob(@RequestParam String schedulerXml) throws Exception {
		
		Document doc = mxUtils.parseXml(schedulerXml);
		Element root = doc.getDocumentElement();
		
		JSONObject jsonObject = JSONObject.fromObject(root.getAttribute("executionConfiguration"));
		
		JobDetail jobDetail = null;
		if(jsonObject.containsKey("start_copy_nr")) {
			jobDetail = newJob(JobRunner.class)
					.withIdentity(root.getAttribute("name"), root.getAttribute("group"))
					.withDescription(root.getAttribute("description")).build();
		} else if(jsonObject.containsKey("exec_cluster")) {
			jobDetail = newJob(TransRunner.class)
					.withIdentity(root.getAttribute("name"), root.getAttribute("group"))
					.withDescription(root.getAttribute("description")).build();
		}
		
		Trigger trigger = null;
		if("simple".equalsIgnoreCase(root.getAttribute("triggerType"))) {
			Date startTime = XMLHandler.stringToDate(root.getAttribute("startTime"));
			if(startTime == null) startTime = new Date();
			trigger = newTrigger()
					.startAt(startTime)
					.endAt(XMLHandler.stringToDate(root.getAttribute("endTime")))
					.withSchedule(
							simpleSchedule()
							.withIntervalInSeconds(Const.toInt(root.getAttribute("interval"), 0))
							.withRepeatCount(Const.toInt(root.getAttribute("repeat"), 0)))
					.forJob(jobDetail)
					.build();
		} else if("cron".equalsIgnoreCase(root.getAttribute("triggerType"))) {
			trigger = newTrigger()
					.withSchedule(cronSchedule(root.getAttribute("cron")))
					.forJob(jobDetail)
					.build();
		}
		
		jobDetail.getJobDataMap().put("executionConfiguration", root.getAttribute("executionConfiguration"));
		scheduler.scheduleJob(jobDetail, trigger);
		
		JsonUtils.success("系统提示", "成功加入调度计划！");
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/executionConfiguration")
	protected void executionConfiguration(@RequestParam String name, @RequestParam String group) throws Exception {
		JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name, group));
		String executionConfiguration = jobDetail.getJobDataMap().getString("executionConfiguration");
		JSONObject jsonObject = JSONObject.fromObject(executionConfiguration);
		if(jobDetail.getJobClass().equals(JobRunner.class))
			jsonObject.put("executionDialog", "JobExecutionConfigurationDialog");
		else if(jobDetail.getJobClass().equals(TransRunner.class))
			jsonObject.put("executionDialog", "TransExecutionConfigurationDialog");
		
		JsonUtils.response(jsonObject);
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/execute")
	protected void execute(@RequestParam String name, @RequestParam String group, @RequestParam String executionConfiguration) throws Exception {
		JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name, group));
		JobDataMap dataMap = (JobDataMap) jobDetail.getJobDataMap().clone();
		dataMap.put("executionConfiguration", executionConfiguration);
		scheduler.triggerJob(new JobKey(name, group), dataMap);
	}
	
	protected JobDetail getJobDetail(String name) throws SchedulerException {
		JobDetail jobDetail = null;
		for(String group : scheduler.getJobGroupNames()) {
			jobDetail = scheduler.getJobDetail(new JobKey(name, group));
			if(jobDetail != null)
				break;
		}
		if(jobDetail == null)
			jobDetail = scheduler.getJobDetail(new JobKey(name));
		return jobDetail;
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/load")
	protected void load(@RequestParam String name) throws Exception {
		JobDetail jobDetail = getJobDetail(name);
		
		Document doc = mxUtils.createDocument();
		Element root = doc.createElement("scheduler");
		if(jobDetail == null) {
			root.setAttribute("name", name);
			root.setAttribute("group", Scheduler.DEFAULT_GROUP);
			
			root.setAttribute("triggerType", "simple");
		} else {
			root.setAttribute("name", jobDetail.getKey().getName());
			root.setAttribute("group", jobDetail.getKey().getGroup());
			root.setAttribute("description", StringEscapeHelper.encode(jobDetail.getDescription()));
			
			Trigger trigger = scheduler.getTriggersOfJob(jobDetail.getKey()).get(0);
			if(trigger instanceof SimpleTrigger) {
				SimpleTrigger st = (SimpleTrigger) trigger;
				root.setAttribute("triggerType", "simple");
				root.setAttribute("startTime", XMLHandler.date2string(st.getStartTime()));
				root.setAttribute("endTime", XMLHandler.date2string(st.getEndTime()));
				root.setAttribute("interval", st.getRepeatInterval() + "");
				root.setAttribute("repeat", st.getRepeatCount() + "");
			} else if(trigger instanceof CronTrigger) {
				CronTrigger ct = (CronTrigger) trigger;
				root.setAttribute("triggerType", "cron");
				root.setAttribute("cron", ct.getCronExpression());
			}
			
		}
		
		JsonUtils.responseXml(mxUtils.getXml(root));
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/groups")
	protected void groups() throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		for(String group : scheduler.getJobGroupNames()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("group", group);
			jsonArray.add(jsonObject);
		}
		
		JsonUtils.response(jsonArray);
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/pause")
	protected void pause(@RequestParam String jobs) throws Exception {
		
		JSONArray jsonArray = JSONArray.fromObject(jobs);
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			scheduler.pauseJob(new JobKey(jsonObject.optString("name"), jsonObject.optString("group")));
		}
		
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/pauseAll")
	protected void pauseAll() throws Exception {
		scheduler.pauseAll();
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/resume")
	protected void resume(@RequestParam String jobs) throws Exception {
		JSONArray jsonArray = JSONArray.fromObject(jobs);
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			scheduler.resumeJob(new JobKey(jsonObject.optString("name"), jsonObject.optString("group")));
		}
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/resumeAll")
	protected void resumeAll() throws Exception {
		scheduler.resumeAll();
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/remove")
	protected void remove(@RequestParam String jobs) throws Exception {
		JSONArray jsonArray = JSONArray.fromObject(jobs);
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			scheduler.deleteJob(new JobKey(jsonObject.optString("name"), jsonObject.optString("group")));
		}
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/logDetail")
	protected void logDetail(@RequestParam String fireId) throws Exception {
		Map record = executionLogDao.findById(fireId);
		String jobName = (String) record.get("jobName");
		
		String dir = jobName.substring(0, jobName.lastIndexOf("/"));
		String name = jobName.substring(jobName.lastIndexOf("/") + 1);
		
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		JobDetail jobDetail = getJobDetail(jobName);
		
		
		JSONObject jsonObject = new JSONObject();
		if(jobDetail.getJobClass().equals(TransRunner.class)) {
			jsonObject.put("GraphType", "TransGraph");
			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
			GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
			String graphXml = codec.encode(transMeta);
			
			jsonObject.put("graphXml", StringEscapeHelper.encode(graphXml));
		} else if(jobDetail.getJobClass().equals(JobRunner.class)) {
			jsonObject.put("GraphType", "JobGraph");
			
			JobMeta jobMeta = repository.loadJob(name, directory, null, null);
			GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.JOB_CODEC);
			String graphXml = codec.encode(jobMeta);
			
			jsonObject.put("graphXml", StringEscapeHelper.encode(graphXml));
		}
		
		jsonObject.put("executionLog", record.get("executionLog"));
		
		JsonUtils.response(jsonObject);
	}
	
	@Autowired
	private ExecutionLogDao executionLogDao;
	
	@ResponseBody
	@RequestMapping(method={RequestMethod.POST, RequestMethod.GET}, value="/jobs")
	protected void jobs() throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		for(String group : scheduler.getJobGroupNames()) {
			GroupMatcher matcher = GroupMatcher.groupEquals(group);
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
			for(JobKey jobKey : jobKeys) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", jobKey.getName());
				jsonObject.put("group", jobKey.getGroup());
				
				String lastestStatus = executionLogDao.lastestStatus(jobKey.getName());
				jsonObject.put("lastestStatus", lastestStatus);
				
				Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);
				jsonObject.put("previousFireTime", XMLHandler.date2string(trigger.getPreviousFireTime()));
				jsonObject.put("nextFireTime", XMLHandler.date2string(trigger.getNextFireTime()));
				
				TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				jsonObject.put("triggerState", triggerState.name());
				
				jsonArray.add(jsonObject);
			}
		}
		
		JsonUtils.response(jsonArray);
	}
	
	@ResponseBody
	@RequestMapping(method={RequestMethod.POST, RequestMethod.GET}, value="/list")
	protected void list(@RequestParam String jobName) throws Exception {
		List list = executionLogDao.list(jobName);
		JSONArray jsonArray = JSONArray.fromObject(list);
		
		JsonUtils.response(jsonArray);
	}
}
