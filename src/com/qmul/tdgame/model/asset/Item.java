/**
 * 
 */
package com.qmul.tdgame.model.asset;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Placeable;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
/**
 * Items Represents: The weapon inventory available to the player.
 * All Items in the game have are static and can't move between Tiles.
 * With the exception of {@link MovingObject}s that have a composite relationship 
 * with an item. Item is the super class that has several sub classes. 
 * @author Imran
 *
 */
public abstract class Item extends Placeable {

	private static final String TAG = Item.class.getSimpleName();
	private final Class<?> type;
	private final Bitmap bitmap;
	
	private State gameState;
	
	private int price;
	private int upgradePrice;
	private boolean upgraded;
	
	
	private int health;
	private boolean destroyed;
	
	private long damageDelay;
	private long lastDamageRecieved;



	private Rect rect;
	private float angle;
	private boolean maxrotating;
	private boolean targetLocked;
	private float effectRadius;
	private float startingHealth;
	private Rect healthBar;
	
	/**
	 * Create a new Item at the given Tile Position. 
	 * @param T The tile where the Item will be placed. 
	 */
	public Item(Tile T, Bitmap bitmap, int initialHealth, int effect_r) {
		super(T);
		type = this.getClass();
		this.bitmap = bitmap;
		health = initialHealth;

		destroyed = false;
		effectRadius = effect_r;
		gameState = State.BUILD;
		
		startingHealth = health;
		int left = (int) getTILE().x;
		int right = left + RENDERABLE_WIDTH - 2;
		int top = (int) getTILE().y - 12;
		int bottom = top + 3;
		
		healthBar = new Rect(left, top, right, bottom);
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the angle
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}

	/**
	 * @return the effectRadius
	 */
	public float getEffectRadius() {
		return effectRadius;
	}

	/**
	 * @param effectRadius the effectRadius to set
	 */
	public void setEffectRadius(float effectRadius) {
		this.effectRadius = effectRadius;
	}

	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setState(State gameState) {
		this.gameState = gameState;
	}
	
	public State getGameState(){
		return gameState;
	}

	/**
	 * @return the maxrotating
	 */
	public boolean isMaxrotating() {
		return maxrotating;
	}

	/**
	 * @param maxrotating the maxrotating to set
	 */
	public void setMaxrotating(boolean maxrotating) {
		this.maxrotating = maxrotating;
	}

	/**
	 * @return the targetLocked
	 */
	public boolean targetLocked() {
		return targetLocked;
	}

	/**
	 * @param targetLocked the targetLocked to set
	 */
	public void setTargetLocked(boolean targetLocked) {
		this.targetLocked = targetLocked;
	}

	/**
	 * @return the destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * @param destroyed the destroyed to set
	 */
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}
	
	/**
	 * @return the lastDamageDealt
	 */
	public long getLastDamageDealt() {
		return lastDamageRecieved;
	}

	/**
	 * @param lastDamageDealt the lastDamageDealt to set
	 */
	public void setLastDamageDealt(long lastDamageDealt) {
		this.lastDamageRecieved = lastDamageDealt;
	}

	/**
	 * @return the damageDelay
	 */
	public long getDamageDelay() {
		return damageDelay;
	}

	/**
	 * @param damageDelay the damageDelay to set
	 */
	public void setDamageDelay(long damageDelay) {
		this.damageDelay = damageDelay;
	}
	
	/**
	 * @return the rect
	 */
	public final Rect getRect() {
		return rect;
	}

	/**
	 * @param rect the rect to set
	 */
	public final void setRect(Rect rect) {
		this.rect = rect;
	}
	
	public void setUpgradePrice(int upgradePrice){
		this.upgradePrice = upgradePrice;
	}
	public int getUpgradePrice(){
		return upgradePrice;
	}
	
	/**
	 * @return the upgraded
	 */
	public final boolean isUpgraded() {
		return upgraded;
	}

	/**
	 * @param upgraded the upgraded to set
	 */
	public final void setUpgraded(boolean upgraded) {
		this.upgraded = upgraded;
	}

	/**
	 * Check if the item has collided with an enemy.
	 */
	public void checkItemCollision() {
		for(Enemy e: GameController.getController().getGame().getCurrentWave().getEnemies()){
			if(e.getRect().intersect(rect)){
				if((System.currentTimeMillis() - lastDamageRecieved) >= damageDelay){
					setHealth(getHealth() -  e.getDamage());
					lastDamageRecieved = System.currentTimeMillis();
				}
				e.setDead(true);
			}
		}
	}

	/**
	 * Upgrade the attributes of the item.
	 */
	public void upgrade() {
		damageDelay += 500;
		price += price;
		health += health + startingHealth;
		upgraded = true;
	}
	
	

	/**
	 * Update the health bars of each enemy.
	 */
	protected void updateHealthBar(){
		
		float healthState =  (float)health / (float)startingHealth ;
		Log.d(TAG, "STARTING = " + startingHealth);
		Log.d(TAG, "CURRENT HEALTH = " + health);
		Log.d(TAG, "HEALTH STATE = " + healthState);
		
		if(healthState < 0.25f){
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;
			
			int right = left +  (RENDERABLE_WIDTH /4);
			
			healthBar.set(left, top, right, bottom);
		}else if(healthState < 0.75f){
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;
			
			int right = left + (RENDERABLE_WIDTH / 2);			
			healthBar.set(left, top, right, bottom);
		}else{
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;
			
			int right = left + (RENDERABLE_WIDTH - 10);
			healthBar.set(left, top, right, bottom);
		}

	}
	
	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		float healthState =  (float)health / (float)startingHealth ;
		if(healthState < 0.25f){
			canvas.drawRect(healthBar, PaintBank.LOW_HEALTH.getPaint());
		}else if(healthState < 0.75f){
			canvas.drawRect(healthBar, PaintBank.MIDDLE_HEALTH.getPaint());
		}else{
			canvas.drawRect(healthBar, PaintBank.GOOD_HEALTH.getPaint());
		}
	}
}
