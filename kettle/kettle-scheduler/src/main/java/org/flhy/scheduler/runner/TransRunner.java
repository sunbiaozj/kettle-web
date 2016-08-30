package org.flhy.scheduler.runner;

import org.flhy.ext.App;
import org.flhy.ext.TransExecutor;
import org.flhy.ext.trans.TransExecutionConfigurationCodec;
import org.flhy.ext.utils.JSONObject;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class TransRunner implements StatefulJob {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			String path = context.getJobDetail().getKey().getName();
			String dir = path.substring(0, path.lastIndexOf("/"));
			String name = path.substring(path.lastIndexOf("/") + 1);
			
			Repository repository = App.getInstance().getRepository();
			RepositoryDirectoryInterface directory = repository.findDirectory(dir);
			if(directory == null)
				directory = repository.getUserHomeDirectory();
			
			TransMeta transMeta = App.getInstance().getRepository().loadTransformation(name, directory, null, true, null);
			
			JSONObject jsonObject = JSONObject.fromObject(context.getMergedJobDataMap().getString("executionConfiguration"));
			TransExecutionConfiguration transExecutionConfiguration = TransExecutionConfigurationCodec.decode(jsonObject, transMeta);
		    
		    TransExecutor transExecutor = TransExecutor.initExecutor(transExecutionConfiguration, transMeta);
		    Thread tr = new Thread(transExecutor, "TransExecutor_" + transExecutor.getExecutionId());
		    tr.start();
		    
		    while(!transExecutor.isFinished()) {
		    	Thread.sleep(1000);
		    }
		    
		    JSONObject result = new JSONObject();
		    result.put("finished", transExecutor.isFinished());
			if(transExecutor.isFinished()) {
				result.put("stepMeasure", transExecutor.getStepMeasure());
				result.put("log", transExecutor.getExecutionLog());
				result.put("stepStatus", transExecutor.getStepStatus());
			} else {
				result.put("stepMeasure", transExecutor.getStepMeasure());
				result.put("log", transExecutor.getExecutionLog());
				result.put("stepStatus", transExecutor.getStepStatus());
			}
			String execMethod = "本地";
			if(transExecutionConfiguration.isExecutingRemotely())
				execMethod = "远程:" + transExecutionConfiguration.getRemoteServer().getName();
			else if(transExecutionConfiguration.isExecutingClustered())
				execMethod = "集群";
			context.getMergedJobDataMap().put("execMethod",  execMethod );
			context.getMergedJobDataMap().put("error", transExecutor.getErrCount());
			context.getMergedJobDataMap().put("executionLog", result.toString());
		    
		} catch(Exception e) {
			throw new JobExecutionException(e);
		}
		
	}

}
