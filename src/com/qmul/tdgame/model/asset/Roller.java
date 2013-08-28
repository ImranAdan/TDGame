/**
 * 
 */
package com.qmul.tdgame.model.asset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.util.DeviceResources;

/**
 * This class represents the roller enemy type. This enemy type is specialised in the 
 * seeking behaviour.
 * @author Imran
 *
 */
public class Roller extends Enemy {

	public static final String TAG = Roller.class.getSimpleName();
	public static final Bitmap BITMAP;
	private static final Paint PAINT;
	 

	/**
	 * define class constants.
	 */
	static{
		Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.roller);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		PAINT = PaintBank.ENEMY_SPWANS.getPaint();
	}
	
		
	/**
	 * Create a new Roller Enemy at an initial starting location.
	 * @param bitmap
	 * @param initialTile
	 * @param initialHealth
	 */
	public Roller(Tile initialTile) {
		super(BITMAP, initialTile);
	}

	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Updateable#update(T[])
	 */
	@Override
	public <T> void update(T... properties) {
		if (getHealth() > 0) {
			if(GameController.getController().getGame().getGameState() ==  State.EARTHQUAKE){
				Log.d("ENEMY", "x= " + GameController.getController().accX + " y= " + GameController.getController().accY);
				
				getPosition().x += GameController.getController().accX;
				getPosition().y += GameController.getController().accY;
				if(outOfBounds())
					setDead(true);
				updateHealthBar();
				
			}else{
			
				switch (getState()) {
				case PATH_FOLLOWING:
					followPath();
					break;
				case SEEKING:
					seek();
					break;
				case STILL:
					standStill();
					break;
				}
			}
			updateHealthBar();
			} else{
				setDead(true);
			}
		
	}
	
	/**
	 * Wait
	 */
	private void standStill() {
		setSpeed(0);
		move();
	}

	/**
	 * Follow a path.
	 */
	private void followPath() {
		if(this.getX_path().size()-1 > 0){
			this.getPosition().x = this.getX_path().remove(0);
			this.getPosition().y = this.getY_path().remove(0);
			float angle = (float) (Math.atan2(this.getY_path().get(0) - getPosition().y, this.getX_path().get(0) - getPosition().x) * 180 / Math.PI);
			setAngle(angle);
		}
	}

	/**
	 * Implement seeking behaviour.
	 */
	private void seek() {
		if (getClosest() == null) {
			seekClosestTarget();
			if(getState() == State.STILL)
				return;
		}
		if (atTarget()) {
			attack();
		} else {
			moveTowardsTarget();
		}
	}

	/**
	 * Indicate whether at target.
	 * @return
	 */
	private boolean atTarget() {
		return getRect().intersect(getClosest().getTILE().getTileRect());
	}
	
	/**
	 * Attack the target
	 */
	private void attack() {	
		if(getClosest().protectedByShield()){
			getClosest().getCurrentShield().setHealth(getClosest().getCurrentShield().getHealth() - getDamage());
			if(getClosest().getCurrentShield().getHealth() <= 0){
				getClosest().getCurrentShield().setDestroyed(true);
			}
		}else{
			getClosest().setHealth(getClosest().getHealth() - getDamage());
			if(getClosest().getHealth() <= 0){
				getClosest().setDead(true);
				setKillStreak(getKillStreak() + 1);
				GameController.getController().getGame().getCurrentWave().updatRollerSuccesses(getKillStreak());
				setClosest(null);
			}
		}
	}
	
	/**
	 * Move towards the target
	 */
	private void moveTowardsTarget() {
		setVelocity(Vector2D.subtract(getClosest().getVector(), getPosition()));
		Vector2D.normalise(getVelocity());
		setAngle(targetAngle());
		move();
	}
	
	private void move() {
		getPosition().x = getPosition().x + (getVelocity().x* getSpeed());
		getPosition().y = getPosition().y + (getVelocity().y* getSpeed());
		
		// Update underlying rectangle
		int left = (int) this.getPosition().x;
		int top = (int) this.getPosition().y;
		int right = left + RENDERABLE_WIDTH;
		int bottom = top + RENDERABLE_HEIGHT;
		getRect().set(left,top,right,bottom);
	}


	/* (non-Javadoc)
	 * @see com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate((float)getAngle(), (getPosition().getX() + (getBitmap().getWidth() / 2)), (getPosition().getY() + (getBitmap().getHeight() / 2)));
		canvas.drawBitmap(getBitmap(), getPosition().getX(), getPosition().getY(), null);
		//canvas.drawRect(getRect(), PAINT);
		canvas.restore();
		super.draw(canvas);
	}
	
	/**
	 * Get the angle of the target 
	 * @return
	 */
	private float targetAngle(){
		float x =   getClosest().getTILE().getTileRect().centerX() - getPosition().x ;
		float y = -(getClosest().getTILE().getTileRect().centerY() -  getPosition().y );
		double rads = Math.atan2(y, x);
		if(rads < 0) rads = Math.abs(rads);
		else rads = 2 * Math.PI - rads;
		return (float) Math.toDegrees(rads);
	}

}
