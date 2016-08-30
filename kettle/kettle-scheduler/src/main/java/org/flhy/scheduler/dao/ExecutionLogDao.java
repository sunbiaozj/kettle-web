package org.flhy.scheduler.dao;

import java.util.List;
import java.util.Map;

public interface ExecutionLogDao {

	public String lastestStatus(String jobName);
	public Map findById(String fireId);
	public List list(String jobName);
	
}
