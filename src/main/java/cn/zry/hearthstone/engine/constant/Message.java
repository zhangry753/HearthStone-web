package cn.zry.hearthstone.engine.constant;

public enum Message {
	/**
	 * 存活
	 */
	ALIVE,
	/**
	 * 死亡
	 */
	DEAD,
	/**
	 * 手牌已满，爆牌了
	 */
	OUT_OF_HAND,
	/**
	 * 场上随从已满
	 */
	OUT_OF_FIELD,
	/**
	 * 法力水晶不够
	 */
	NO_ENOUGH_MANA,
	/**
	 * 随从攻击随从，双方均死亡
	 */
	SERVANT_ALL_DEAD,
	/**
	 * 随从攻击随从，只有我方死亡
	 */
	SERVANT_SELF_DEAD,
	/**
	 * 随从攻击随从，只有对方死亡
	 */
	SERVANT_OPP_DEAD,
}
