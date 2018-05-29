package cn.zry.hearthstone.engine.action;

import cn.zry.hearthstone.engine.constant.Consts;
import cn.zry.hearthstone.engine.constant.Message;
import cn.zry.hearthstone.engine.constant.Target;
import cn.zry.hearthstone.engine.entity.Situation;
import cn.zry.hearthstone.engine.entity.card.Card;
import cn.zry.hearthstone.engine.entity.card.ServantCard;
import cn.zry.hearthstone.engine.utils.TargetUtil;

public class MainAction {
	
	/**
	 * 抽牌
	 * @param user 抽牌者的situation
	 * @return 抽牌者是否存活
	 */
	public static Message draw(Situation user) {
		Card newCard = user.getLibrary().poll();
		if(newCard==null){ //牌库抽空，造成伤害
			user.setTiredness(user.getTiredness()+1);
			return MainAction.harm(user, user.getTiredness());
		}else{
			if(user.getHand().size()>=Consts.MAX_HAND){ //手牌已到上限
				user.getGraveyard().add(newCard);
				return Message.OUT_OF_HAND;
			}else{
				user.getHand().add(newCard);
				return Message.ALIVE;
			}
		}
	}
	
	/**
	 * 召唤仆从(无附加选目标的效果)
	 * @param user 召唤者
	 * @param card	仆从牌
	 * @param target 召唤位置
	 * @return
	 */
	public static Message callServant(Situation user, ServantCard card, Target target) throws Exception{
		int position = TargetUtil.transToField(target);
		if(position >= 0){
			if(user.getField().size() >= Consts.MAX_TABLE) //战场中随从达到上限
				throw new Exception("战场中随从达到上限");
//				return Message.OUT_OF_FIELD;
			if(user.getCrystal() < card.getCost()) //法力水晶不够
				throw new Exception("法力水晶不够");
//				return Message.NO_ENOUGH_MANA;
			if(position > user.getField().size())
				position = user.getField().size();
			//将该随从牌置入战场
			user.getHand().remove(card);
			user.setCrystal(user.getCrystal()-card.getCost());
			user.getField().add(position, card);
			return Message.ALIVE;
		}else {
			throw new Exception("仆从召唤位置错误");
		}
	}
	
	/**
	 * 造成伤害
	 * @param user 受伤害的目标
	 * @return 目标是否存活
	 */
	public static Message harm(Situation user, int damagePoint) {
		if(damagePoint<=0)
			return Message.ALIVE;
		user.setHealth(user.getHealth()-damagePoint);
		return user.getHealth()>0? Message.ALIVE: Message.DEAD;
	}

	/**
	 * 随从对随从攻击
	 * @param curUser 当前角色
	 * @param oppUser 对方目标
	 * @param origServantIndex 攻击者fieldIndex
	 * @param destServantIndex 被攻击者fieldIndex
	 * @return 随从死亡情况
	 */
	public static Message attackServant(Situation curUser, Situation oppUser, 
			int origServantIndex, int destServantIndex) {
		if(origServantIndex<0 || origServantIndex>curUser.getField().size()
				|| destServantIndex<0 || destServantIndex>oppUser.getField().size())
			return Message.ALIVE;
		ServantCard origServant = curUser.getField().get(origServantIndex);
		ServantCard destServant = oppUser.getField().get(destServantIndex);
		destServant.setHealth(destServant.getHealth()-origServant.getAtk());
		origServant.setHealth(origServant.getHealth()-destServant.getAtk());
		origServant.setCanAttack(false);
		if(origServant.getHealth()<=0 && destServant.getHealth()<=0){ //双方随从均死亡
			curUser.getField().remove(origServantIndex);
			curUser.getGraveyard().add(origServant);
			oppUser.getField().remove(destServantIndex);
			curUser.getGraveyard().add(destServant);
			return Message.SERVANT_ALL_DEAD;
		}else if(origServant.getHealth()<=0){ //只有我方随从死亡
			curUser.getField().remove(origServantIndex);
			curUser.getGraveyard().add(origServant);
			return Message.SERVANT_SELF_DEAD;
		}else if(destServant.getHealth()<=0){ //只有对方随从死亡
			oppUser.getField().remove(destServantIndex);
			curUser.getGraveyard().add(destServant);
			return Message.SERVANT_OPP_DEAD;
		}else { //没有随从死亡
			return Message.ALIVE;
		}
		
	}
}
