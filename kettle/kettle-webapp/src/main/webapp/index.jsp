<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<style>
	#contextmenu {
		border: 1px solid #aaa;
		border-bottom: 0;
		background: #eee;
		position: absolute;
		list-style: none;
		margin: 0;
		padding: 0;
		display: none;
	}
																			   
	#contextmenu li a {
		display: block;
		padding: 10px;
		border-bottom: 1px solid #aaa;
		cursor: pointer;
	}
																			   
	#contextmenu li a:hover {
		background: #fff;
	}
</style>
<html>
	<head>
	  	<title>KettleConsole</title>
	  	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/resources/css/ext-all.css" />
	  	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ui/css/public.css" />
	</head>
	<body>
		<ul id="contextmenu" style="display:none;">    
		    <li><a>新建注释</a></li>
		    <li><a>从剪贴板粘贴步骤</a></li>    
		    <li><a>全选</a></li>
		    <li><a>清除选择</a></li>
		    <li><a>查看图形文件</a></li>    
		    <li><a>查看引擎文件</a></li>
		    <li><a>转换设置</a></li>
	    </ul>
		<div id="loading-mask">
			<input type="hidden" id="context-path" value="${pageContext.request.contextPath}" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/other/init.js"></script>
		</div>
		<div id="loading">
		    <div class="loading-indicator">
		        <img src="ui/resources/extanim32.gif" width="32" height="32" style="margin-right:8px;" align="absmiddle" />
				系统加载中，请稍后...
		    </div>
		</div>
		<!-- 在线代码编辑器 -->
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CodeMirror/codemirror.css" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/CodeMirror/codemirror.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/CodeMirror/javascript.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/CodeMirror/sql.js"></script>
	    
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/ux/ext-patch.css" />
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/ux/treegrid/treegrid.css" />
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ui/css/system.css2" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/mxgraph2/js/mxClient3.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/adapter/ext/ext-base.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ext-all-debug.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/CheckColumn.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/ListBox.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/ConditionEditor.js"></script>
	    
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/ux/fileupload/css/fileuploadfield.css" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/fileupload/FileUploadField.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/DynamicEditorGrid.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/treegrid/TreeGridNodeUI.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/treegrid/TreeGrid.js"></script>
	    
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/ux/datetime/Spinner.css" />
	    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ext3/ux/datetime/datetime.css" />
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/datetime/Spinner.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/datetime/SpinnerField.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ext3/ux/datetime/datetime.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/ui/global.js2"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogTransPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogStepPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogRunningPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogChannelPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogMetricsPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransParamTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransLogTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransDateTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransDependenciesTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransMiscTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransMonitoringTab.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/TransExecutionConfigurationDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/ClusterSchemaDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/PartitionSchemaDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/trans/StepErrorMetaDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/job/JobExecutionConfigurationDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/NormalPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/AdvancePanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/OptionsPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/PoolPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/ClusterPanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/DatabaseDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/database/DatabaseExplorerDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/KettleDatabaseRepositoryDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/KettleFileRepositoryDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/RepositoriesDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/RepositoryCheckTree.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/RepositoryTree.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/repository/RepositoryManageDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/scheduler/SchedulerDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/scheduler/SchedulerLogDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/scheduler/SchedulerManageDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/StepFieldsDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/SQLStatementsDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/CheckResultDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/EnterTextDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/EnterSelectionDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dialogs/EnterValueDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/BaseGraph.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/TransGraph.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/JobGraph.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/KettleDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/TransResult.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/JobResult.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/graph/SlaveServerDialog.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/other/TextAreaDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/other/AnswerDialog.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/other/FileExplorerWindow.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/GuidePanel.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/initMain.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/initStore.js"></script>
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/jTopo/jquery-3.1.0.min.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/jTopo/jtopo-0.4.8-min.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/jTopo/canvas.js"></script>
	    
	    
	    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jtopoEvent/tranEvent.js"></script>
	</body>
</html>