package cn.zry.hearthstone;

import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.zry.hearthstone.engine.HSGame;
import cn.zry.hearthstone.engine.constant.Target;
import cn.zry.hearthstone.engine.entity.Situation;
import cn.zry.hearthstone.engine.entity.card.Card;
import cn.zry.hearthstone.engine.entity.card.CardType;
import cn.zry.hearthstone.engine.entity.card.ServantCard;

@RestController
public class MainController {
	private HSGame game = new HSGame();
	private Gson gson = new Gson();
	private int user1Id = 1;
	private int user2Id = 2;
	
	@GetMapping("/restart")
	public String restart() {
		JsonObject result = new JsonObject();
		result.addProperty("status", "ok");
		try {
			game.start(user1Id, user2Id);
		} catch (Exception e) {
			result.addProperty("status", "error");
			result.addProperty("msg", e.getMessage());
		}
		return result.toString();
	}
	
	@GetMapping("/getInfo")
	public String getInfo() {
		JsonObject result = new JsonObject();
		result.addProperty("status", "ok");
		try {
			Situation[] situations = game.getSituation();
			Situation curUser = situations[0];
			Situation oppoUser = situations[1];
			JsonObject data = new JsonObject();
			data.addProperty("curUser", gson.toJson(curUser));
			data.addProperty("oppoUser", gson.toJson(oppoUser));
			result.add("data", data);
		} catch (Exception e) {
			result.addProperty("status", "error");
			result.addProperty("msg", e.getMessage());
		}
		return result.toString();
	}

	@PostMapping("/useCard")
	public String useCard(@RequestParam int cardIndex, @RequestParam Target target) {
		JsonObject result = new JsonObject();
		result.addProperty("status", "ok");
		int winnerId = -1;
		try {
			winnerId = game.useCard(user1Id, cardIndex, target);
		} catch (Exception e) {
			result.addProperty("status", "error");
			result.addProperty("msg", e.getMessage());
		}
		result.addProperty("winner", winnerId);
		return result.toString();
	}
	
	@PostMapping("/servantAttack")
	public String servantAttack(@RequestParam Target cardIndex, @RequestParam Target target) {
		JsonObject result = new JsonObject();
		result.addProperty("status", "ok");
		int winnerId = -1;
		try {
			winnerId = game.servantAttack(user1Id, cardIndex,target);
		} catch (Exception e) {
			result.addProperty("status", "error");
			result.addProperty("msg", e.getMessage());
		}
		result.addProperty("winner", winnerId);
		return result.toString();
	}

	@PostMapping("/endTurn")
	public String endTurn() {
		JsonObject result = new JsonObject();
		result.addProperty("status", "ok");
		int winnerId = -1;
		JsonArray log = new JsonArray();
		try {
			game.endTurn(user1Id);
			//---------------------------------电脑回合----------------------------------
			Situation curUser = game.getSituation()[0];
			int remainMana = curUser.getCrystal();
			//手牌随从全上场
			for(int i=curUser.getHand().size()-1; i>=0; i--){
				Card baseCard = curUser.getHand().get(i);
				if(baseCard.getCardType()!=CardType.SERVANT)
					continue;
				ServantCard card = (ServantCard) baseCard;
				if(remainMana>=card.getCost() && curUser.getField().size()<7) {
					winnerId = game.useCard(curUser.getUserId(), i, Target.SELF_FIELD_RIGHT);
					log.add("对手召唤了“" + card.getName() + "”");
					remainMana -= card.getCost();
				}
			}
			//场上随从全攻击，随机指定目标
			for(int i=curUser.getField().size()-1; i>=0; i--){
				ServantCard card = curUser.getField().get(i);
				Situation oppoUser = game.getSituation()[1];
				if(oppoUser.getField().size()==0 || Math.random()>0.5) {
					winnerId = game.servantAttack(curUser.getUserId(), Target.values()[i+2], Target.OPPONENT);
					log.add("对手使用“"+ card.getName() +"”攻击了你的英雄");
				}else{
					int targetIndex = (int)(Math.random() * oppoUser.getField().size() + 10);
					ServantCard targetCard = oppoUser.getField().get(targetIndex-10);
					game.servantAttack(curUser.getUserId(), Target.values()[i+2], Target.values()[targetIndex]);
					log.add("对手使用“"+ card.getName() +"”攻击了“"+ targetCard.getName() +"”");
				}
			}
			game.endTurn(curUser.getUserId());
			//---------------------------------电脑回合----------------------------------
		} catch (Exception e) {
			result.addProperty("status", "error");
			result.addProperty("msg", e.getMessage());
		}
		game.changeTurn(user1Id);
		result.add("log", log);
		result.addProperty("winner", winnerId);
		return result.toString();
	}
}
