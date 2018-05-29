package cn.zry.hearthstone.engine.entity.card;

public class ServantCard extends Card {
	//基础数据
	private int atkBase;  //基础攻击力(牌面攻击力)
	private int atk;  //实际攻击力(各种buff)
	private int healthBase;  //基础生命值上限(牌面生命值)
	private int healthMax;  //实际生命值上限(各种buff)
	private int health;  //当前剩余生命值
	//额外数据
	//状态
	private boolean isFreeze;
	private boolean canAttack;
	
	
	
	public ServantCard(String name, int cost, int atk, int health){
		super(name, cost);
		this.atkBase = atk;
		this.atk = this.atkBase;
		this.healthBase = health;
		this.healthMax = this.health;
		this.health = this.healthBase;
		this.cardType = CardType.SERVANT;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.cost+"费"+this.atk+"-"+this.health;
	}
	
	@Override
	public ServantCard clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (ServantCard)super.clone();
	}
	
	public int getAtkBase() {
		return atkBase;
	}
	public void setAtkBase(int atkBase) {
		this.atkBase = atkBase;
	}
	public int getAtk() {
		return atk;
	}
	public void setAtk(int atk) {
		this.atk = atk;
	}
	public int getHealthBase() {
		return healthBase;
	}
	public void setHealthBase(int healthBase) {
		this.healthBase = healthBase;
	}
	public int getHealthMax() {
		return healthMax;
	}
	public void setHealthMax(int healthMax) {
		this.healthMax = healthMax;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isFreeze() {
		return isFreeze;
	}

	public void setFreeze(boolean isFreeze) {
		this.isFreeze = isFreeze;
	}

	public boolean isCanAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}
	
	
	
}
