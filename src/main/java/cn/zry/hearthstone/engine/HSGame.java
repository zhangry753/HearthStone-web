package cn.zry.hearthstone.engine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.zry.hearthstone.engine.action.MainAction;
import cn.zry.hearthstone.engine.constant.Message;
import cn.zry.hearthstone.engine.constant.Target;
import cn.zry.hearthstone.engine.entity.Situation;
import cn.zry.hearthstone.engine.entity.card.Card;
import cn.zry.hearthstone.engine.entity.card.CardType;
import cn.zry.hearthstone.engine.entity.card.MagicCard;
import cn.zry.hearthstone.engine.entity.card.ServantCard;
import cn.zry.hearthstone.engine.utils.TargetUtil;


public class HSGame {
	//玩家牌面状态
	private Situation curUser;
	private Situation oppUser;
	//游戏是否已结束
	private boolean isGameFinish;
	private int winnerId;
	//卡牌名
	private String[] cardNames = new String[]{
		"愤怒的小鸡","淡水鳄","铁鬓灰熊","冰风雪人","荆棘谷猛虎",
		"石拳食人魔","巨型作战傀儡","狂奔的魔暴龙","贪睡巨龙","死亡之翼"
	};
	
	//--------------------------------------------------------------------------------------
	//-------------------------------- 各种操作的函数  ---------------------------------------
	//--------------------------------------------------------------------------------------
	public void start(int user1Id, int user2Id) throws Exception{
		if(user1Id<=0 || user2Id<=0)
			throw new Exception("用户id需大于0");
		this.curUser = new Situation(user1Id);
		this.oppUser = new Situation(user2Id);
		this.isGameFinish = false;
		this.winnerId = -1;
		//刷新牌库
		List<Card> user1Lib = new ArrayList<>();
		List<Card> user2Lib = new ArrayList<>();
		for(int i=0;i<15;i++){
			int cost = (int)(Math.random()*10+1);
			user1Lib.add(new ServantCard(cardNames[cost-1], cost, cost, cost));
			cost = (int)(Math.random()*10+1);
			user2Lib.add(new ServantCard(cardNames[cost-1], cost, cost, cost));
		}
		 Collections.shuffle(user1Lib);
		 Collections.shuffle(user2Lib);
		 curUser.getLibrary().addAll(user1Lib);
		 oppUser.getLibrary().addAll(user2Lib);
		 //抽牌
		 for(int i=0;i<3;i++){
			 MainAction.draw(curUser);
		 }
		 for(int i=0;i<4;i++){
			 MainAction.draw(oppUser);
		 }
		 //回合开始抽一张牌,法力水晶+1
		 MainAction.draw(curUser);
		 curUser.setCrystalMax(curUser.getCrystalMax()+1);
		 curUser.setCrystal(curUser.getCrystalMax());
	}
	
	/**
	 * 获取对局信息
	 * @return 信息集合，0--当前玩家牌面信息；1--对手玩家牌面信息
	 * @throws Exception
	 */
	public Situation[] getSituation(){
		Situation situations[] = new Situation[2];
		try {
			situations[0] = curUser.clone();
			situations[1] = oppUser.clone();
			//隐藏牌库信息和对手手牌信息
			situations[0].setLibraryCount(situations[0].getLibrary().size());
			situations[0].getLibrary().clear();
			situations[0].setHandCount(situations[0].getHand().size());
			situations[1].setLibraryCount(situations[1].getLibrary().size());
			situations[1].getLibrary().clear();
			situations[1].setHandCount(situations[1].getHand().size());
			situations[1].getHand().clear();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return situations;
	}
	
	/**
	 * 使用卡牌(无目标)
	 * @param userId	使用者id
	 * @param cardIndex	使用第几张卡牌,序号从0开始
	 * @return	如果没有人获胜则返回-1，如果有则返回获胜者id
	 */
	public int useCard(int userId, int cardIndex) throws Exception{
		if(isGameFinish)
			throw new Exception("游戏未开始或游戏已结束");
		if(curUser.getUserId()!=userId)
			throw new Exception("当前不是该玩家回合");
		cardIndex = clipIndex(cardIndex, 0, curUser.getHand().size()-1);
		Card card = curUser.getHand().get(cardIndex);
		//--------todo:使用卡牌---------------------------------------
		curUser.getHand().remove(cardIndex);
		isGameFinish = winnerId>0;
		return winnerId;
	}

	/**
	 * 使用卡牌(有目标)
	 * @param userId	使用者id
	 * @param cardIndex	使用第几张卡牌,序号从0开始
	 * @param target	卡牌目标
	 * @return	如果没有人获胜则返回-1，如果有则返回获胜者id
	 */
	public int useCard(int userId, int cardIndex, Target target) throws Exception{
		if(isGameFinish)
			throw new Exception("游戏未开始或游戏已结束");
		if(curUser.getUserId()!=userId)
			throw new Exception("当前不是该玩家回合");
		cardIndex = clipIndex(cardIndex, 0, curUser.getHand().size()-1);
		Card baseCard = curUser.getHand().get(cardIndex);
		// 随从牌------------------------------------------
		if(baseCard.getCardType()==CardType.SERVANT){
			ServantCard card = (ServantCard)baseCard;
			Message actionResult = MainAction.callServant(curUser, card, target);
			if(winnerId<0 && actionResult==Message.DEAD){
				winnerId = curUser.getUserId();
				isGameFinish = true;
				return winnerId;
			}
		}
		// 法术牌------------------------------------------
		if(baseCard.getCardType()==CardType.MAGIC){
			MagicCard card = (MagicCard)baseCard;
			//--------todo:使用卡牌----------------------------------
		}
		isGameFinish = winnerId>0;
		return winnerId;
	}
	
	/**
	 * 随从攻击
	 * @param userId 操作者id
	 * @param servant 使用的随从
	 * @param dest 攻击目标
	 * @return 如果没有人获胜则返回-1，如果有则返回获胜者id
	 * @throws Exception
	 */
	public int servantAttack(int userId, Target orig, Target dest) throws Exception{
		if(isGameFinish)
			throw new Exception("游戏未开始或游戏已结束");
		if(curUser.getUserId()!=userId)
			throw new Exception("当前不是该玩家回合");
		int cardIndex = TargetUtil.transToSelfServant(orig);
		if(cardIndex<0 || cardIndex>=curUser.getField().size())
			throw new Exception("攻击源选择错误");
		ServantCard servantCard = curUser.getField().get(cardIndex);
		if(!servantCard.isCanAttack()) //随从无法攻击
			throw new Exception("该随从无法攻击");
		int targetIndex = TargetUtil.transToOppoServant(dest);
		if(dest==Target.OPPONENT){ //攻击敌方英雄
			Message actionResult = MainAction.harm(oppUser, servantCard.getAtk());
			servantCard.setCanAttack(false);
			if(winnerId<0 && actionResult==Message.DEAD){
				winnerId = curUser.getUserId();
				isGameFinish = true;
				return winnerId;
			}
		}else if(targetIndex>=0){ //攻击敌方随从
			targetIndex = clipIndex(targetIndex, 0, oppUser.getField().size()-1);
			Message atackResult = MainAction.attackServant(curUser, oppUser, cardIndex, targetIndex);
			servantCard.setCanAttack(false);
		}else{
			throw new Exception("攻击目标选择错误");
		}
		return winnerId;
	}
	
	/**
	 * 回合结束(下一回合)
	 * @param userId 操作者id
	 * @return	如果没有人获胜则返回-1，如果有则返回获胜者id
	 */
	public int endTurn(int userId)  throws Exception{
		if(isGameFinish)
			throw new Exception("游戏未开始或游戏已结束");
		if(curUser.getUserId()!=userId)
			throw new Exception("当前不是该玩家回合");
		//刷新对手随从的攻击
		for (ServantCard card : oppUser.getField()) {
			if(!card.isFreeze())
				card.setCanAttack(true);
		}
		//对手抽一张牌,法力水晶+1
		Message actionResult = MainAction.draw(oppUser);
		if(winnerId<0 && actionResult==Message.DEAD){
			winnerId = curUser.getUserId();
			isGameFinish = true;
			return winnerId;
		}
		if(oppUser.getCrystalMax()<10)
			oppUser.setCrystalMax(oppUser.getCrystalMax()+1);
		oppUser.setCrystal(oppUser.getCrystalMax());
		//跳转到对手回合
		Situation temp = oppUser;
		oppUser = curUser;
		curUser = temp;
		return winnerId;
	}
	
	/**
	 * 强制跳转为用户回合，只做状态改变，用于对手回合结束游戏时执行
	 * @param userId 跳转到该用户回合
	 */
	public void changeTurn(int userId) {
		if(curUser!=null && oppUser!=null) {
			if(oppUser.getUserId()==userId) {
				Situation temp = oppUser;
				oppUser = curUser;
				curUser = temp;
			}
		}
	}
	
	public int getWinnerId() {
		return winnerId;
	}
	
	private int clipIndex(int value, int min, int max) throws Exception{
		if(min>max)
			throw new Exception("列表为空");
		if(value<min)
			value = min;
		if(value>max)
			value = max;
		return value;
	}
}
