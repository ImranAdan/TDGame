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
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;
import com.qmul.tdgame.util.DeviceResources;
import com.qmul.tdgame.util.RandomGenerator;

/**
 * The class which represents the a driller enemy type.
 * @author Imran
 *
 */
public class Driller extends Enemy{
	
	public static final String TAG = Driller.class.getSimpleName();
	public static final Bitmap BITMAP;
	private static final Paint PAINT;
	

	/**
	 * Define class constants
	 */
	static{
		Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.drill);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, b.getHeight(), true);
		PAINT = PaintBank.ENEMY_SPWANS.getPaint();
	}
	
	/**
	 * Create a new Driller object at an initial tile.
	 * @param initialTile
	 */
	public Driller(Tile initialTile) {
		super(BITMAP, initialTile);
	}

	@Override
	public <T> void update(T... properties) {
		if (getHealth() > 0) {
			if(GameController.getController().getGame().getGameState() ==  State.EARTHQUAKE){
				Log.d("ENEMY-D", "x= " + GameController.getController().accX + " y= " + GameController.getController().accY);
				getPosition().x += GameController.getController().accX;
				getPosition().y += GameController.getController().accY;
				if(outOfBounds())
					setDead(true);
				
				if(getState() != State.SEEKING){
					setState(State.SEEKING);
				}
				updateHealthBar();
			}
			else {
				checkPathTarget();
			switch (this.getState()) {
			case PATH_FOLLOWING:
				followPath();
				break;
			case SEEKING:
				if (!isNearestAvailableTowerFound()) {
					searchForNearestTower();
				}
				seek();
				break;
			}

			// Update underlying rectangle
			int left = (int) this.getPosition().x;
			int top = (int) this.getPosition().y;
			int right = left + RENDERABLE_WIDTH;
			int bottom = top + RENDERABLE_HEIGHT;
			getRect().set(left, top, right, bottom);
			
			updateHealthBar();
		}
	
			
		}else{
			setDead(true);
		}
	}
	
	/**
	 * Check if the path target is still alive. If not 
	 * then change the state of this enemy to seeking.
	 */
	private void checkPathTarget() {
		if(getState() == State.PATH_FOLLOWING){
			if(!GameController.getController().getGame().getPlayerTowers().contains(getClosest())){
				setNearestAvailableTowerFound(false);
				setState(State.SEEKING);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.rotate((float)getAngle(), (getPosition().getX() + (getBitmap().getWidth() / 2)), (getPosition().getY() + (getBitmap().getHeight() / 2)));
		canvas.drawBitmap(getBitmap(), getPosition().getX(), getPosition().getY(), null);
	//	canvas.drawRect(getRect(), PAINT);
		canvas.restore();
		
		super.draw(canvas);
	}
	
	/**
	 * Follow the path.
	 */
	private void followPath() {
		if(this.getX_path().size()-1 > 0){
			this.getPosition().x = this.getX_path().remove(0);
			this.getPosition().y = this.getY_path().remove(0);
			float angle = (float) (Math.atan2(this.getY_path().get(0) - getPosition().y, this.getX_path().get(0) - getPosition().x) * 180 / Math.PI);
			setAngle(angle);
			if(atTarget())
				attack();
		}else{
			setState(State.SEEKING);
		}
	}


		

	/**
	 * Search for the nearest target.
	 */
	private void searchForNearestTower() {
		if(GameController.getController().getGame().getPlayerTowers().size()<=0){
			setNearestAvailableTowerFound(false);
			return;
		}
		
		setClosest(GameController.getController().getGame().getPlayerTowers().get(0));
		for(int i = 1; i<GameController.getController().getGame().getPlayerTowers().size(); i++)
			if(Math.abs(getPosition().x -GameController.getController().getGame().getPlayerTowers().get(i).getTILE().x + Math.abs(getPosition().y - GameController.getController().getGame().getPlayerTowers().get(i).getTILE().y)) 
					< Math.abs(getPosition().x - getClosest().getTILE().x + Math.abs(getPosition().y - getClosest().getTILE().y)))
				setClosest(GameController.getController().getGame().getPlayerTowers().get(i));
		setNearestAvailableTowerFound(true);
	}

	// ---  Movement Algorithms ---//    
	

	/**
	 * Implement the seeking behaviours.
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

	/**
	 * Return the angle which the enemy is at.
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
	
	
	/**
	 * Seek the closest target.
	 */
	public void seekClosestTarget() {
		
		if(GameController.getController().getGame().getPlayerTowers().size() <= 0){
			this.setState(State.STILL);
			return;
		}
		
		Tower closestTarget = GameController.getController().getGame().getPlayerTowers().get(0);
		setClosest(closestTarget);
		float currentClosest = Vector2D.length(Vector2D.subtract(new Vector2D(closestTarget.getTILE().getTileRect().centerX(), closestTarget.getTILE().getTileRect().centerY()), this.getPosition()));
		for(int i = 1; i < GameController.getController().getGame().getPlayerTowers().size(); i++){
			Tower nextTower = GameController.getController().getGame().getPlayerTowers().get(i);
			float currentDistance = Vector2D.length(Vector2D.subtract(new Vector2D(nextTower.getTILE().getTileRect().centerX(), nextTower.getTILE().getTileRect().centerY()), this.getPosition()));			
			if(currentDistance < currentClosest){
				setClosest(nextTower);
				currentClosest = currentDistance;
			}
		}
	}
	
	/**
	 * At the target.
	 * @return
	 */
	private boolean atTarget() {
		return getRect().intersect(getClosest().getTILE().getTileRect());
	}
	
	/**
	 * Attack the target.
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
				setKillStreak(getKillStreak() + 1);
				GameController.getController().getGame().getCurrentWave().updatDrillerSuccesses(getKillStreak());
				getClosest().setDead(true);
				setClosest(null);
			}
		}
	}
}
