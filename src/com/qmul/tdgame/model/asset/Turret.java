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

import com.qmul.tdgame.R;
import com.qmul.tdgame.view.SoundPlayer;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;
import com.qmul.tdgame.util.DeviceResources;
import com.qmul.tdgame.util.GameResources;

/**
 * This class represents a turret object. A turret object can fire projectiles
 * at enemies.
 * @author Imran
 *
 */
public class Turret extends Item {

	private static final String TAG = Turret.class.getSimpleName();
	public static String getTag() { return TAG; }
	
	private static final int UPGRADE_PRICE = 200;
	public static final int INITIAL_STARING_PRICE;
	private static final int INITIAL_TURRET_HEALTH;
	private static final int INITIAL_EFFECT_RADIUS;
	private static final float MAX_ANGLE_TURN;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;
	
	/**
	 * Define class constants
	 */
	static{
		INITIAL_STARING_PRICE = 250;
		INITIAL_TURRET_HEALTH = 500;
		Bitmap b =  BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.turret_up);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		INITIAL_EFFECT_RADIUS = Renderable.RENDERABLE_WIDTH*3;
		MAX_ANGLE_TURN = 180.0f;
		PAINT = PaintBank.EFFECT_RADIUS.getPaint();
	}
	

	
	private long bulletDelay;
	private long lastBulletTime;
	private Enemy currentTarget;
	private List<Bullet> bullets;
	private int bulletDamage;

	
	/**
	 * Create a new Turret object at a given Tile. 
	 * @param T Tile
	 */
	public Turret(Tile T) {
		super(T, BITMAP, INITIAL_TURRET_HEALTH, INITIAL_EFFECT_RADIUS);
		setPrice(INITIAL_STARING_PRICE);
		setTargetLocked(false);
		setMaxrotating(false);
		setCurrentTarget(null);
		setRect(new Rect(getTILE().x, getTILE().y, getTILE().x + RENDERABLE_WIDTH, getTILE().y + RENDERABLE_HEIGHT));
		setDamageDelay(1000);
		setUpgradePrice(UPGRADE_PRICE);
		
		// Setup Bullet Inventory.
		bulletDamage = Bullet.INIT_BULLET_DAMAGE;
		bullets = new CopyOnWriteArrayList<Bullet>();
		setBulletDelay(1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qmul.tdgame.model.interfaces.Updateable#update(T[])
	 */
	@Override
	public <T> void update(T... properties) {
		if (getHealth() > 0) {
			if (!GameController.getController().getGame().isWaveStarted()) {
				lookaround();
				if(bullets.size() > 0)
					updateBullets();
			} else {
				checkItemCollision();
				if (targetLocked()) {
					if (withinRange(currentTarget) && !currentTarget.isDead()) {
						faceTarget();
						shoot();
					} else {
						currentTarget = null;
						setTargetLocked(false);
					}
				} else {
					if (searchFoundTarget())
						return;
					else{
						lookaround();
					}
				}
			}
			updateHealthBar();
			updateBullets();
		}
		else {
			setDestroyed(true);
		}
	}

	
	
	public void upgrade() {
		setEffectRadius(getEffectRadius() + 100);
		bulletDelay /= 2;
		bulletDamage += 50;
		for (Bullet b : bullets)
			b.damage = bulletDamage;
		super.upgrade();
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
			canvas.restore();
		}
		else{
			canvas.rotate((float)getAngle(), (getTILE().x + (getBitmap().getWidth() / 2)), (getTILE().y + (getBitmap().getHeight() / 2)));
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
			canvas.restore();
		}
		
		for(Bullet b: bullets){
			b.draw(canvas);
		}
		
		super.draw(canvas);
	}


	/**
	 * Indicate whether an enemy is within range.
	 * @param e
	 * @return
	 */
	private boolean withinRange(Enemy e){
		float a = Math.abs(e.getPosition().x) - Math.abs(getTILE().getTileRect().centerX());
		float b = Math.abs(e.getPosition().y) - Math.abs(getTILE().getTileRect().centerY());
		a = a*a;
		b = b*b;
		float c = (float) Math.sqrt(a + b) - e.getBitmap().getWidth();	
		return (c<=getEffectRadius()) ? true: false;	
	}
	
	/**
	 * Retunr the target's angle.
	 * @return
	 */
	private float targetAngle(){
		float x = currentTarget.getPosition().x - getTILE().getTileRect().centerX();
		float y = -(currentTarget.getPosition().y - getTILE().getTileRect().centerY());
		double rads = Math.atan2(y, x);
		if(rads < 0) rads = Math.abs(rads);
		else rads = 2 * Math.PI - rads;
		return (float) Math.toDegrees(rads);
	}


	/**
	 * Update the bullets that are fired by the turret.
	 */
	private void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).bulletDestroyed) {
				bullets.remove(i);
				i--;
			} else{
				if(currentTarget != null)
					bullets.get(i).update(currentTarget);
				else
					bullets.get(i).update();
			}
		}
	}

	/**
	 * Face the target.
	 */
	private void faceTarget() {
		setAngle(targetAngle());
	}
	
	/**
	 * Shoot the target.
	 */
	private void shoot() {
		// Shoot Target;		
		if((System.currentTimeMillis() - lastBulletTime) >= bulletDelay){	
//			Vector2D bulletPosition = new Vector2D(this.getTILE().x, this.getTILE().y);
//			Vector2D bulletVelocity = Vector2D.angleAsVector(getAngle());	
			Vector2D bulletPosition = new Vector2D(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY());
			Vector2D bulletVelocity = Vector2D.subtract(currentTarget.getPosition(), bulletPosition);
			Vector2D.normalise(bulletVelocity);
			bullets.add(new Bullet(bulletPosition, bulletVelocity));
			lastBulletTime = System.currentTimeMillis();
		}	
	}

	/**
	 * The turret looks around for 180 degrees.
	 */
	private void lookaround() {
		if (getAngle() < MAX_ANGLE_TURN && !isMaxrotating()) {
			setAngle(getAngle() + 1f);
			if (getAngle() >= MAX_ANGLE_TURN)
				setMaxrotating(true);
		} else {
			setAngle(getAngle() - 1f);
			if (getAngle() <= 0f)
				setMaxrotating(false);
		}
	}



	
	/**
	 * Lock target by iterating through the enemy collections and returning the enemy that 
	 * intersects within the items radius. Using the formula
	 * 
	 * Difference = abs.enemyX^2 - abs.itemX^2 + abs.enemyY^2 - abs.itemY^2
	 * 
	 * @return boolean to indicate whether at target has been locked. 
	 */
	private boolean searchFoundTarget() {
		for (Enemy e : GameController.getController().getGame().getCurrentWave().getEnemies()) {
			if (withinRange(e)) {
				setCurrentTarget(e);
				setTargetLocked(true);
				return true;
			}
		}
		return false;
	}

	

	
	
	/**
	 * @return the bulletDelay
	 */
	public long getBulletDelay() {
		return bulletDelay;
	}

	/**
	 * @param bulletDelay the bulletDelay to set
	 */
	public void setBulletDelay(long bulletDelay) {
		this.bulletDelay = bulletDelay;
	}


	/**
	 * @return the lastBulletTime
	 */
	public long getLastBulletTime() {
		return lastBulletTime;
	}

	/**
	 * @param lastBulletTime the lastBulletTime to set
	 */
	public void setLastBulletTime(long lastBulletTime) {
		this.lastBulletTime = lastBulletTime;
	}


	protected Turret getCurrentInstance(){
		return this;
	}
	

	
	/**
	 * @return the currentTarget
	 */
	public Enemy getCurrentTarget() {
		return currentTarget;
	}

	/**
	 * @param currentTarget the currentTarget to set
	 */
	public void setCurrentTarget(Enemy currentTarget) {
		this.currentTarget = currentTarget;
	}
	
	/**
	 * Static nested class that represents bullet.
	 * @author Imran
	 *
	 */
	private static class Bullet implements Updateable, Renderable{

		private static final String TAG = Bullet.class.getSimpleName();
		private static final int MIN_X_LOCATION = 0;
		private static final int MIN_Y_LOCATION = 0;
		private static final int MAX_X_LOCATION = GameResources.getDrawingPanelWidth();
		private static final int MAX_Y_LOCATION = GameResources.getDrawingPanelHeight();
		
		private static final float BULLET_SPEED = 1f;
		private static final int BULLET_WIDTH = RENDERABLE_WIDTH/2;
		private static final int BULLET_HEIGHT = RENDERABLE_HEIGHT/2;
		private static final Bitmap BITMAP;
		private static final Paint PAINT;
		
		private static final int INIT_BULLET_DAMAGE = 100;
		
		/**
		 * Define class constants
		 */
		static{
			PAINT = PaintBank.ENEMY_SPWANS.getPaint();
			Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.bullet);
			BITMAP = Bitmap.createScaledBitmap(b, BULLET_WIDTH, BULLET_HEIGHT, true);
		}
		
		private int damage;
		private boolean bulletDestroyed;
		private Vector2D position;
		private Vector2D velocity;
		private Rect rect;
		private boolean shot;
		
		
		/**
		 * Create a new bullet at a starting location with a starting 
		 * velocity.
		 * @param p
		 * @param v
		 */
		public Bullet(Vector2D p, Vector2D v){
			damage = INIT_BULLET_DAMAGE;
			bulletDestroyed = false;
			position = p;
			velocity = v;
			int left = (int) position.x;
			int top = (int) position.y;
			int right = left + BULLET_WIDTH;
			int bottom =  top + BULLET_HEIGHT;
			rect = new Rect(left, top, right, bottom);
			shot = false;
		}
		
		
	
		@Override
		public <T> void update(T... properties) {
			if (properties.length > 0) {
				Enemy e = (Enemy) properties[0];
				if (rect.intersect(e.getRect())) {
					e.setHealth(e.getHealth() - damage);
					if (e.getHealth() <= 0) {
						e.setDead(true);
					}
					bulletDestroyed = true;
				}
			}
			
			if(GameController.getController().getGame().getCurrentWave().getEnemies().size() > 0)
				checkForBulletCollisions(GameController.getController().getGame().getCurrentWave().getEnemies());
			
			move();
		}

		/**
		 * Check for bullet collisions.
		 * @param enemies
		 */
		private void checkForBulletCollisions(List<Enemy> enemies) {
			for(Enemy e: enemies)
				if(!e.equals(this)){
					if(rect.intersect(e.getRect())){
						e.setHealth(e.getHealth() - damage);
						if (e.getHealth() <= 0) {
							e.setDead(true);
						}
						bulletDestroyed = true;
					}
				}
		}


		private void move() {	
			position.x = position.x + (velocity.x * BULLET_SPEED);
			position.y = position.y + (velocity.y * BULLET_SPEED);
			
			int left = (int) position.x;
			int top = (int) position.y;
			int right =  left + BULLET_WIDTH;
			int bottom = top + BULLET_HEIGHT;	
			rect.set(left, top, right, bottom);
			
			if(position.x > MAX_X_LOCATION || position.y > MAX_Y_LOCATION || position.x < MIN_X_LOCATION || position.y < MIN_Y_LOCATION){
				bulletDestroyed = true;
			}
			
		}
		
		@Override
		public void draw(Canvas canvas) {
			canvas.drawBitmap(BITMAP, position.x, position.y, null);
			if(!shot){
				GameController.getController().playSFX(SoundPlayer.SFX.SOUND_SHOT);
				shot = true;
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Bullet [bulletDestroyed=" + bulletDestroyed + ", position="
					+ position + ", velocity=" + velocity + ", rect=" + rect
					+ "]";
		}
	}
}
