/**
 * 
 */
package com.qmul.tdgame.model.asset;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.util.DeviceResources;

/**
 * This class represents a mine item. The mine items 
 * draws enemies in and applies damage to each effected enemy.
 * @author Imran
 *
 */
public class Mine extends Item {

	private static final String TAG = Mine.class.getSimpleName();
	public static String getTag() { return TAG; }
	
	public static final int UPGRADE_PRICE = 250;
	public static final int INITIAL_STARING_PRICE;
	private static final int INITIAL_MINE_HEALTH;	
	private static final int INITIAL_EFFECT_RADIUS;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;
	
	/**
	 * define class constants
	 */
	static{
		INITIAL_STARING_PRICE = 300;
		INITIAL_MINE_HEALTH = 1000;
		Bitmap b =  BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.mine);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		INITIAL_EFFECT_RADIUS = Renderable.RENDERABLE_WIDTH*2;
		PAINT = PaintBank.EFFECT_RADIUS.getPaint();
	}
	
	
	private Paint paint;
	private Vector2D positionVector;
	
	private List<Enemy> nearByEnemies;

	private long damageDelay;
	private long lastDealtDamage;
	
	private int mineDamage;
	
	/**
	 * Create a new Mine object, the mine object has a force
	 * that drags enemies to it, destroying them upon collision.
	 * @param T The tile the 
	 * @param bitmap
	 * @param initialHealth
	 * @param effect_r
	 */
	public Mine(Tile T) {
		super(T, BITMAP, INITIAL_MINE_HEALTH, INITIAL_EFFECT_RADIUS);
		setPrice(INITIAL_STARING_PRICE);
		setUpgradePrice(UPGRADE_PRICE);
		positionVector = new Vector2D(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY());
		setRect(new Rect(this.getTILE().x, this.getTILE().y, this.getTILE().x + RENDERABLE_WIDTH, this.getTILE().y + RENDERABLE_HEIGHT));
		paint = PaintBank.ENEMY_SPWANS.getPaint();
		setDamageDelay(750);
		mineDamage = 1;
		nearByEnemies = new CopyOnWriteArrayList<Enemy>();
		setUpgradePrice(UPGRADE_PRICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qmul.tdgame.model.interfaces.Updateable#update(T[])
	 */
	@Override
	public <T> void update(T... properties) {
		if(getHealth() > 0){
			if(GameController.getController().getGame().isWaveStarted()){
				checkForEnemies();
				applyEffectOnEnemies();
			}
			setAngle(getAngle() + 5f);
			updateHealthBar();
		}else{
			setDestroyed(true);
		}
	}

	/**
	 * Apply the damage on the collection of enemies.
	 */
	private void applyEffectOnEnemies() {
		for (int i = 0; i < nearByEnemies.size(); i++) {
			if(nearByEnemies.get(i).getHealth() <= 0){
				nearByEnemies.get(i).setDead(true);
				nearByEnemies.remove(i);
				i--;
			}else{
				Vector2D diff = Vector2D.subtract(nearByEnemies.get(i).getPosition(), positionVector);
				Vector2D.normalise(diff);
				nearByEnemies.get(i).getPosition().x -= diff.x * 2;
				nearByEnemies.get(i).getPosition().y -= diff.y * 2;
				nearByEnemies.get(i).setHealth(nearByEnemies.get(i).getHealth() - mineDamage);
				if ((System.currentTimeMillis() - lastDealtDamage) >= damageDelay) {
					setHealth(this.getHealth() - nearByEnemies.get(i).getDamage());
					lastDealtDamage = System.currentTimeMillis();
				}
			}
		}
	}

	/**
	 * check for enemies that are within the effect radius.
	 */
	private void checkForEnemies() {
		for (Enemy e : GameController.getController().getGame().getCurrentWave().getEnemies()) {
			if (withinRange(e)) {
				if(!nearByEnemies.contains(e))
					nearByEnemies.add(e);
			}else{
				if(nearByEnemies.contains(e))
					nearByEnemies.remove(e);
			}
		}		
	}

	/**
	 * Check if a specifc enemy is in the range.
	 * @param e
	 * @return
	 */
	private boolean withinRange(Enemy e) {
		float a = Math.abs(e.getPosition().x) - Math.abs(getTILE().getTileRect().centerX());
		float b = Math.abs(e.getPosition().y) - Math.abs(getTILE().getTileRect().centerY());
		a = a*a;
		b = b*b;
		float c = (float) Math.sqrt(a + b) - e.getBitmap().getWidth();	
		return (c<=getEffectRadius()) ? true: false;	
	}

	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		if(getGameState() == State.BUILD){
			canvas.drawCircle(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY(), getEffectRadius(), PAINT);
			canvas.rotate(getAngle(), (getTILE().x + (getBitmap().getWidth() / 2)), (getTILE().y + (getBitmap().getHeight() / 2)));
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
		}
		else{
			canvas.rotate((float)getAngle(), (getTILE().x + (getBitmap().getWidth() / 2)), (getTILE().y + (getBitmap().getHeight() / 2)));
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
		}
		canvas.restore();
		
		super.draw(canvas);
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
	 * @return the lastDealtDamage
	 */
	public long getLastDealtDamage() {
		return lastDealtDamage;
	}

	/**
	 * @param lastDealtDamage the lastDealtDamage to set
	 */
	public void setLastDealtDamage(long lastDealtDamage) {
		this.lastDealtDamage = lastDealtDamage;
	}
	
	public void upgrade(){
		mineDamage *= 5;
		setEffectRadius(getEffectRadius() + 50);
		super.upgrade();
	}
}
