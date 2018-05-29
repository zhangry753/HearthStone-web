$(function(){
// 重启游戏按钮
$("#restartBtn").click(function(){
	$.ajax({
		url:"/restart",
		success:function(data){
			data = JSON.parse(data);
			if(data["status"]!="ok"){
				layer.msg("游戏启动失败！\n"+data["msg"]);
			}else{
				$("#playground").show();
				$("#log").html("");
				$("#log").append("<p class='self-log'>游戏开始<br/></p>")
				updateScene();
			}
		}
	});
});
//手牌点击事件
$(".hand-card").click(function(){
	if($(this).hasClass("empty-card"))
		return;
	if(!$(this).hasClass("card-select")){
		$(".card-select").removeClass("card-select");
		$(this).addClass("card-select");
	}else
		$(this).removeClass("card-select");
});
//战场仆从点击事件
$(".battle-card").click(function(){
	//使用卡牌，step2，选择目标
	if($("#handCard .card-select").length>0 && $(this).hasClass("empty-card")){ 
		var cardIndex = $("#handCard .card-select").attr("myValue");
		var cardName = $("#handCard .card-select #cardName").html();
		var target = $(this).attr("myValue");
		$.post(
			"/useCard",
			{cardIndex:cardIndex, target:target},
			function(data){
				data = JSON.parse(data);
				if(data["status"]!="ok"){
					layer.msg(data["msg"]);
				}else{
					$("#log").append("<p class='self-log'>你召唤了“"+cardName+"”<br/></p>")
					updateScene();
				}
			}
		);
	}
	//仆从攻击，step1，选择仆从
	else if($(this).parent().attr("id")=="selfBattleYard" && !$(this).hasClass("empty-card")){ 
		if(!$(this).hasClass("card-select")){
			$(".card-select").removeClass("card-select");
			$(this).addClass("card-select");
		}else
			$(this).removeClass("card-select");
	}
	//仆从攻击，step2，选择目标
	else if($("#selfBattleYard .card-select").length>0 && !$(this).hasClass("empty-card")){ 
		var cardIndex = $("#selfBattleYard .card-select").attr("myValue");
		var cardName = $("#selfBattleYard .card-select #cardName").html();
		var target = $(this).attr("myValue");
		var targetCardName = $(this).find("#cardName").html();
		$.post(
			"/servantAttack",
			{cardIndex:cardIndex, target:target},
			function(data){
				data = JSON.parse(data);
				if(data["status"]=="ok"){
					$("#log").append("<p class='self-log'>你使用“"+cardName+"”攻击了“"+targetCardName+"”<br/></p>");
				}else{
					layer.msg(data["msg"]);
				}
				updateScene();
			}
		);
	}
});
//敌方英雄点击事件
$("#opponent").click(function(){
	if($("#selfBattleYard .card-select").length>0){
		var cardIndex = $("#selfBattleYard .card-select").attr("myValue");
		var cardName = $("#selfBattleYard .card-select #cardName").html();
		var target = $(this).attr("myValue");
		$.post(
			"/servantAttack",
			{cardIndex:cardIndex, target:target},
			function(data){
				data = JSON.parse(data);
				if(data["status"]=="ok"){
					$("#log").append("<p class='self-log'>你使用“"+cardName+"”攻击了敌方英雄<br/></p>");
					if(data["winner"]>0){
						$("#log").append("<p class='self-log'>游戏结束<br/></p>");
						layer.alert("游戏结束！你获得胜利。");
					}
				}else{
					layer.msg(data["msg"]);
				}
				updateScene();
			}
		);
	}
});
//回合结束点击事件
$("#endTurn").click(function(){
	$.post("/endTurn",null,function(data){
		data = JSON.parse(data);
		$("#log").append("<p class='self-log'>你结束了回合<br/></p>");
		var htmlStr = "<p class='oppo-log'>";
		for(var i=0;i<data["log"].length;i++){
			htmlStr += data["log"][i] + "<br/>";
		}
		htmlStr += "对手结束了回合<br/></p>";
		$("#log").append(htmlStr);
		updateScene();
		if(data["winner"]>0){
			$("#log").append("<p class='self-log'>游戏结束<br/></p>");
			layer.alert("游戏结束！对手获得胜利。");
		}else if(data["status"]!="ok"){
			layer.msg(data["msg"]);
		}
	});
});

});


// 获取并刷新对局信息
function updateScene(){
	$.ajax({
		url:"/getInfo",
		success:function(data){
			data = JSON.parse(data);
			if(data["status"]=="ok"){
				var curUser = JSON.parse(data["data"]["curUser"]);
				var oppoUser = JSON.parse(data["data"]["oppoUser"]);
				//刷新英雄信息
				$("#selfHealth").html(curUser["health"]);
				$("#selfCurCry").html(curUser["crystal"]);
				$("#selfMaxCry").html(curUser["crystalMax"]);
				$("#selfHandCount").html(curUser["handCount"]);
				$("#selfLibCount").html(curUser["libraryCount"]);
				$("#oppoHealth").html(oppoUser["health"]);
				$("#oppoCurCry").html(oppoUser["crystal"]);
				$("#oppoMaxCry").html(oppoUser["crystalMax"]);
				$("#oppoHandCount").html(oppoUser["handCount"]);
				$("#oppoLibCount").html(oppoUser["libraryCount"]);
				//刷新我方战场
				var selfField = curUser["field"];
				$("#selfBattleYard .battle-card").each(function(i){
					if(i<selfField.length){
						$(this).removeClass("empty-card");
						$(this).html(
							"<p id='cardName'>"+selfField[i].name+"</p><br/>"
							+"攻击力:"+selfField[i].atk+"<br/>"
							+"生命值:"+selfField[i].health
						);
					}else{
						$(this).addClass("empty-card");
						$(this).html("无");
					}
				});
				//刷新敌方战场
				var oppoField = oppoUser["field"]
				$("#oppoBattleYard .battle-card").each(function(i){
					if(i<oppoField.length){
						$(this).removeClass("empty-card");
						$(this).html(
							"<p id='cardName'>"+oppoField[i].name+"</p><br/>"
							+"攻击力:"+oppoField[i].atk+"<br/>"
							+"生命值:"+oppoField[i].health
						);
					}else{
						$(this).addClass("empty-card");
						$(this).html("无");
					}
				});
				//刷新手牌
				var hand = curUser["hand"]
				$("#handCard .hand-card").each(function(i){
					if(i<hand.length){
						$(this).removeClass("empty-card");
						$(this).html(
							"<p id='cardName'>"+hand[i].name+"</p><br/>"
							+"费用:"+hand[i].cost+"<br/>"
							+"攻击力:"+hand[i].atk+"<br/>"
							+"生命值:"+hand[i].health
						);
					}else{
						$(this).addClass("empty-card");
						$(this).html("无");
					}
				});
				//清除操作
				$(".card-select").removeClass("card-select");
			}
		}
	});
	
}