function handler(event,scene){
	//将对象赋值
	canvas = scene;
	//点击的时候将选中取消
	var s = canvas.getDisplayedElements();
	for (var i = 0; i < s.length; i++) {
		s[i].selected = false;
	}
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

var canvas ;

$("#contextmenu a").click(function(){
    var text = $(this).text();
    //获取全部选中
    var selects = canvas.getDisplayedElements();
    
    if(text == '新建注释'){
        alert('新建注释');
    }if(text == '从剪贴板粘贴步骤'){
    	alert('从剪贴板粘贴步骤');
    }if(text == '全选'){
    	for (var i = 0; i < selects.length; i++) {
    		selects[i].selected = true;
		}
//    	alert('新建注释');
    }else if(text == '清除选择'){
    	for (var i = 0; i < selects.length; i++) {
    		selects[i].selected = false;
		}
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