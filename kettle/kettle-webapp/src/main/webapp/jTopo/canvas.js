$(window).resize(resizeCanvas);
function resizeCanvas() {
	
	var canvas = $('#canvas'); 
	if(canvas != undefined){
		canvas.attr("width",$("#pan").innerWidth());  
		canvas.attr("height",$("#pan").innerHeight());  
		
		var context = canvas.get(0).getContext("2d");  
		context.fillRect(0,0,canvas.width(),canvas.height());  
	}
 };