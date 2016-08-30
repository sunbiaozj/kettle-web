create table kettle_execution_log
(
	fireId varchar2(500),
	jobName varchar2(500),
	startTime timestamp,
	endTime timestamp,
	execMethod varchar2(100),
	status varchar2(50),
	executionConfiguration clob,
	executionLog clob
);