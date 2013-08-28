/**
 * 
 */
package com.qmul.tdgame.model.asset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.util.DeviceResources;

/**
 * Wall is a defence type object that is used to block the path of incoming enemy waves.
 * The best way to use walls is when an incoming wave is taking a predetermined path i.e. 
 * when an agent is not fully autonomous.   
 * @author Imran
 *
 */
public class Wall extends Item {
	
	private static final String TAG = Wall.class.getSimpleName();
	
	public static final int INITIAL_STARING_PRICE;
	private static final int UPGRADE_PRICE;
	private static final int INITIAL_WALL_HEALTH;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;
	private static final int EFFECT_R;


	
	static{
		INITIAL_STARING_PRICE = 150;
		INITIAL_WALL_HEALTH = 1000;
		Bitmap b =  BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.wallup);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		PAINT = PaintBank.ENEMY_SPWANS.getPaint();
		EFFECT_R = 0;
		UPGRADE_PRICE = 100;
	}

	private int damageDelay;
	private long lastDealtDamage;
	

	/**
	 * Create a new Wall at the given Tile.
	 * @param T The tile.
	 */
	public Wall(Tile T) {
		super(T, BITMAP, INITIAL_WALL_HEALTH, EFFECT_R);
		setPrice(INITIAL_STARING_PRICE);
		setUpgradePrice(UPGRADE_PRICE);
		damageDelay = 1000;
		setRect(new Rect(getTILE().x, getTILE().y, getTILE().x + RENDERABLE_WIDTH, getTILE().y + RENDERABLE_HEIGHT));
		setUpgradePrice(UPGRADE_PRICE);
	}

	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Updateable#update(T[])
	 */
	@Override
	public <T> void update(T... properties) {	
		if(getHealth() > 0){
			if(GameController.getController().getGame().isWaveStarted())
				checkForWallCollision();
		}else{
			setDestroyed(true);
		}
	}

	/**
	 * Check for wall collisions
	 */
	private void checkForWallCollision() {
		for (Enemy e : GameController.getController().getGame().getCurrentWave().getEnemies()) {
			if(e.getRect().intersect(getRect())){
				if ((System.currentTimeMillis() - lastDealtDamage) >= damageDelay) {
					setHealth(this.getHealth() - e.getDamage());
					lastDealtDamage = System.currentTimeMillis();
				}		
				e.setDead(true);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		if(getGameState() == State.BUILD){
			canvas.drawCircle(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY(), getEffectRadius(), PAINT);
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
		}
		else{
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
		}
	}

	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}
	
	
	public void upgrade(){
		super.upgrade();
	}
}
