function handler(event){
    if(event.button == 2){// 右键
        // 当前位置弹出菜单（div）
        $("#contextmenu").css({
            top: event.pageY,
            left: event.pageX
        }).show();    
    }else{
    	$("#contextmenu").hide();
    }
}


$("#contextmenu a").click(function(){
    var text = $(this).text();
    
    if(text == '新建注释新建注释'){
        alert('新建注释');
    }if(text == '从剪贴板粘贴步骤'){
    	alert('从剪贴板粘贴步骤');
    }if(text == '全选'){
//    	alert('新建注释');
    }else if(text == '清除选择'){
//    	alert('新建注释');
    }else if(text == '查看图形文件'){
    	alert('查看图形文件');
    }else if(text == '查看引擎文件'){
    	alert('查看引擎文件');
    }else if(text == '转换设置'){
    	var transDialog = new TransDialog();
		transDialog.show();
    }
    $("#contextmenu").hide();
});