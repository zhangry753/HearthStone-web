package cn.zry.hearthstone.engine.entity.card;

public class Card implements Cloneable{
	protected int id;  //卡牌id
	protected String name; //卡牌名
	protected String description; //卡牌描述
	protected int cost;  //水晶消耗
	protected CardType cardType;  //卡牌类型
	
	public Card(){}
	public Card(String name, int cost){
		this();
		this.name = name;
		this.cost = cost;
	}
	public Card(String name, String description, int cost){
		this(name, cost);
		this.description = description;
	}
	
	@Override
	public Card clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (Card)super.clone();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCrystal() {
		return cost;
	}
	public void setCrystal(int crystal) {
		this.cost = crystal;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public CardType getCardType() {
		return cardType;
	}
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
