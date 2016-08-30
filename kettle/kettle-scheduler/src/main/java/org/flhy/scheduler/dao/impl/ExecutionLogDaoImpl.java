package org.flhy.scheduler.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.flhy.scheduler.dao.ExecutionLogDao;
import org.pentaho.di.core.xml.XMLHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class ExecutionLogDaoImpl extends JdbcDaoSupport implements ExecutionLogDao {

	@Override
	public String lastestStatus(String jobName) {
		return getJdbcTemplate().query("select * from kettle_execution_log where jobname=? and endTime is not null order by starttime desc", new String[]{jobName}, 
				new ResultSetExtractor<String>() {
					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						if(rs.next())
							return rs.getString("status");
						
						return "-";
					}
				}
		);
		
	}

	@Override
	public List list(String jobName) {
		return getJdbcTemplate().query("select * from kettle_execution_log where jobname=? order by starttime desc", new String[]{jobName}, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
				record.put("fireId", rs.getString(1));
				record.put("jobName", rs.getString(2));
				record.put("startTime", XMLHandler.date2string(rs.getTimestamp(3)));
				record.put("endTime", XMLHandler.date2string(rs.getTimestamp(4)));
				record.put("execMethod", rs.getString(5));
				record.put("status", rs.getString(6));
				return record;
			}
		});
	}

	@Override
	public Map findById(String fireId) {
		return getJdbcTemplate().query("select * from kettle_execution_log where fireId=?", new String[]{fireId}, 
				new ResultSetExtractor<Map>() {
					@Override
					public Map extractData(ResultSet rs) throws SQLException, DataAccessException {
						if(rs.next()) {
							LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
							record.put("fireId", rs.getString(1));
							record.put("jobName", rs.getString(2));
							record.put("startTime", XMLHandler.date2string(rs.getTimestamp(3)));
							record.put("endTime", XMLHandler.date2string(rs.getTimestamp(4)));
							record.put("execMethod", rs.getString(5));
							record.put("status", rs.getString(6));
							record.put("executionConfiguration", rs.getString(7));
							record.put("executionLog", rs.getString(8));
							return record;
						}
						
						return null;
					}
				}
		);
	}

}
