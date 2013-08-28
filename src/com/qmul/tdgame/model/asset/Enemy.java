/**
 * 
 */
package com.qmul.tdgame.model.asset;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;
import com.qmul.tdgame.util.GameResources;
import com.qmul.tdgame.util.RandomGenerator;

/**
 * Abstract class the holds all  attributes that are common to all enemy
 * objects.
 * @author Imran
 *
 */
public abstract class Enemy implements Updateable, Renderable {

	//Specific to Class
	public static final String TAG = Enemy.class.getSimpleName();
	private static final GameMap MAP =GameController.getController().getGame().getMap();
	public static final int INIT_DAMAGE = 100;
	public static final int INIT_HEALTH = 500;
	
	
	//Specific to Object
	private  Bitmap bitmap;
	// State
	private State state;
	
	//Health attributes
	private int startingHealth;


	private int health;
	private Rect healthBar;
	private boolean dead;
	private int deathPrice;
	private int damage;
	private int kill_streak;

	
	private boolean nearestAvailableTowerFound;
	private Tower closest;
	
	// Movement attributes
	private Vector2D position;
	private Vector2D velocity;
	private Rect rect;
	private ArrayList<Float> x_path;
	private ArrayList<Float> y_path;
	private float speed;
	private double angle;
	



	/**
	 * Create a new Enemy.
	 * @param bitmap The bitmap of the enemy.
	 * @param initialTile The initial Tile which the enemy starts at.
	 */
	public Enemy(Bitmap bitmap, Tile initialTile) {
		this.bitmap = bitmap;
		health = INIT_HEALTH;
		setDead(false);
		deathPrice = 25;
		damage = INIT_DAMAGE;
		kill_streak = 0;
		nearestAvailableTowerFound = false;
		closest = null;
		angle = 0;	
		setX_path(new ArrayList<Float>());
		setY_path(new ArrayList<Float>());	
		setRect(new Rect(initialTile.x, initialTile.y, initialTile.x + RENDERABLE_WIDTH, initialTile.y + RENDERABLE_HEIGHT));
		position = new Vector2D(initialTile.x,initialTile.y);
		velocity = new Vector2D(0, 0);
		
		int left = (int) position.x;
		int right = left + RENDERABLE_WIDTH - 2;
		int top = (int) getPosition().y - 12;
		int bottom = top + 3;
		
		healthBar = new Rect(left, top, right, bottom);
	}




	/**
	 * @return the tag
	 */
	public static final String getTag() {
		return TAG;
	}


	/**
	 * @return the map
	 */
	public static final GameMap getMap() {
		return MAP;
	}


	/**
	 * @return the bitmap
	 */
	public final Bitmap getBitmap() {
		return bitmap;
	}


	/**
	 * @return the nearestAvailableTowerFound
	 */
	public final boolean isNearestAvailableTowerFound() {
		return nearestAvailableTowerFound;
	}


	/**
	 * @return the closest
	 */
	public final Tower getClosest() {
		return closest;
	}




	/**
	 * @return the position
	 */
	public final Vector2D getPosition() {
		return position;
	}


	/**
	 * @return the velocity
	 */
	public final Vector2D getVelocity() {
		return velocity;
	}


	/**
	 * @return the angle
	 */
	public final double getAngle() {
		return angle;
	}


	/**
	 * @param bitmap the bitmap to set
	 */
	public final void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}


	/**
	 * @param nearestAvailableTowerFound the nearestAvailableTowerFound to set
	 */
	public final void setNearestAvailableTowerFound(
			boolean nearestAvailableTowerFound) {
		this.nearestAvailableTowerFound = nearestAvailableTowerFound;
	}


	/**
	 * @param closest the closest to set
	 */
	public final void setClosest(Tower closest) {
		this.closest = closest;
	}



	/**
	 * @param position the position to set
	 */
	public final void setPosition(Vector2D position) {
		this.position = position;
	}


	/**
	 * @param velocity the velocity to set
	 */
	public final void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}


	/**
	 * @param angle the angle to set
	 */
	public final void setAngle(double angle) {
		this.angle = angle;
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
	 * @return the state
	 */
	public State getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}


	/**
	 * Create a new Enemy at a controlled random location. Enemies are spawned
	 * either left or right of the game map.
	 * 
	 * @return A new Enemy at random x and y coordinates.
	 */
	public static Enemy spwanAtRandomLocation() {
		int rowNum = RandomGenerator.pickRandomERow(GameController.getController().getGame().getMap().getGame2DGrid().getRows());
		int colNum = RandomGenerator.pickRandomECol(GameController.getController().getGame().getMap().getGame2DGrid().getCols());
		Tile T = GameController.getController().getGame().getMap().getGame2DGrid().getTILES()[rowNum][colNum];
		return new Driller(T);
	}

	public float getSpeed() {
		return speed;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public ArrayList<Float> getX_path() {
		return x_path;
	}


	public void setX_path(ArrayList<Float> x_path) {
		this.x_path = x_path;
	}

	public ArrayList<Float> getY_path() {
		return y_path;
	}


	public void setY_path(ArrayList<Float> y_path) {
		this.y_path = y_path;
	}

	
	
	/**
	 * @return the rect
	 */
	public Rect getRect() {
		return rect;
	}


	/**
	 * @param rect the rect to set
	 */
	public void setRect(Rect rect) {
		this.rect = rect;
	}



	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}




	/**
	 * @param damage the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}



	/**
	 * @return the dead
	 */
	public boolean isDead() {
		return dead;
	}




	/**
	 * @param dead the dead to set
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
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
	 * @return the deathPrice
	 */
	public int getDeathPrice() {
		return deathPrice;
	}




	/**
	 * @param deathPrice the deathPrice to set
	 */
	public void setDeathPrice(int deathPrice) {
		this.deathPrice = deathPrice;
	}

	public int getKillStreak() {
		return kill_streak;
	}


	/**
	 * @param streak the successValue to set
	 */
	public void setKillStreak(int streak) {
		this.kill_streak = streak;
	}
	
	
	public boolean outOfBounds(){
		return position.x > GameResources.getDrawingPanelWidth() || position.y > GameResources.getDrawingPanelHeight() || position.x < 0 || position.y < 0;
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
			int left = (int) position.x;
			int top = (int) getPosition().y - 5;
			int bottom = top + 3;
			
			int right = left +  (RENDERABLE_WIDTH /4);
			
			healthBar.set(left, top, right, bottom);
		}else if(healthState < 0.75f){
			int left = (int) position.x;
			int top = (int) getPosition().y - 5;
			int bottom = top + 3;
			
			int right = left + (RENDERABLE_WIDTH / 2);			
			healthBar.set(left, top, right, bottom);
		}else{
			int left = (int) position.x;
			int top = (int) getPosition().y - 5;
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
	
	
	/**
	 * @return the startingHealth
	 */
	public final int getStartingHealth() {
		return startingHealth;
	}


	/**
	 * @param startingHealth the startingHealth to set
	 */
	public final void setStartingHealth(int startingHealth) {
		this.startingHealth = startingHealth;
	}
}
