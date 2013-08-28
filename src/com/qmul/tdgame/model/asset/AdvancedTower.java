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
 * Advanced Tower class represents an upgraded Tower in the game.
 * @author imran
 *
 */
public class AdvancedTower extends Tower {
	
	private static final String TAG = AdvancedTower.class.getSimpleName();
	public static String getTag() { return TAG; }

	/**
	 * The initial health of all advanced tower objects.
	 */
	public static final int UPGRADE_PRICE = 1250;
	public static final int INITIAL_STARING_PRICE;
	private static final int INITIAL_ADVANCED_TOWER_HEALTH;
	private static final int INITIAL_EFFECT_RADIUS;
	private static final float MAX_ANGLE_TURN;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;
	
	/**
	 * Initialise the classes constants
	 */
	static{
		INITIAL_STARING_PRICE = 2500;
		INITIAL_ADVANCED_TOWER_HEALTH = 1000;		
		Bitmap b =  BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.advanced_tower_up);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		INITIAL_EFFECT_RADIUS = Renderable.RENDERABLE_WIDTH*3;
		MAX_ANGLE_TURN = 360;
		PAINT = PaintBank.EFFECT_RADIUS.getPaint();
	}
	
	private float faceAngle;
	private float effectRadius; 
	
	private boolean targetLocked;
	private boolean maxRotationReached;
	private Enemy currentTarget;

	private int missileDamage;
	private long missileDelay;
	private long lastMissileTime;
	private List<Missile> missiles;

	
	
	/**
	 * Create a new advanced tower.
	 * @param t
	 */
	public AdvancedTower(Tile t) {
		super(t, BITMAP, PAINT, INITIAL_ADVANCED_TOWER_HEALTH);
		setPrice(INITIAL_STARING_PRICE);
		faceAngle = 0;
		effectRadius = INITIAL_EFFECT_RADIUS;
		targetLocked = false;
		maxRotationReached = false;
		currentTarget = null;
		setRect(new Rect(getTILE().x, getTILE().y, getTILE().x + RENDERABLE_WIDTH, getTILE().y + RENDERABLE_HEIGHT));
		setDamageDelay(1000);
		missiles = new CopyOnWriteArrayList<Missile>();
		missileDelay = 750;
		missileDamage = Missile.INIT_MISSILE_DAMAGE;
	}
	
	@Override
	public <T> void update(T... properties) {
		if(getHealth() > 0){
			if (!GameController.getController().getGame().isWaveStarted()) {
				lookaround();
				if(missiles.size() > 0)
					updateMissiles();
			} else {
				checkTowerCollision();
				if (targetLocked) {
					if (withinRange(currentTarget) && !currentTarget.isDead()) {
						faceAngle = targetAngle();
						shoot();
					} else {
						currentTarget = null;
						targetLocked = false;
					}
				} else {
					if (targetFound())
						return;
					else{
						lookaround();
					}
				}
			}
			updateHealthBar();
			updateMissiles();
		}else{
			setDead(true);
		}
	}
	
	
	public void upgrade(){
		effectRadius += 100;
		missileDelay = 400;
		missileDamage += 100;
		for(Missile m: missiles)
			m.damage = missileDamage;
		super.upgrade();
	}
	
	/**
	 * Return true if the tower has found a target.
	 * @return
	 */
	private boolean targetFound() {
		for (Enemy e : GameController.getController().getGame().getCurrentWave().getEnemies()) {
			if (withinRange(e)) {
				currentTarget = e;
				targetLocked = true;
				return true;
			}
		}
		return false;
	}

	

	/**
	 * Look around.
	 */
	private void lookaround() {
		if (faceAngle < MAX_ANGLE_TURN && !maxRotationReached) {
			faceAngle += 1f;
			if (faceAngle >= MAX_ANGLE_TURN)
				maxRotationReached = true;
		} else {
			faceAngle -= 1f;
			if (faceAngle <= 0f)
				maxRotationReached = false;
		}
	}
	
	/**
	 * Update the missiles.
	 */
	private void updateMissiles() {
		for (int i = 0; i < missiles.size(); i++) {
			if (missiles.get(i).missileDestroyed) {
				missiles.remove(i);
				i--;
			} else{
				if(currentTarget != null)
					missiles.get(i).update(currentTarget);
				else
					missiles.get(i).update();
			}
		}
	}
	
	private boolean withinRange(Enemy e){
		float a = Math.abs(e.getPosition().x) - Math.abs(getTILE().getTileRect().centerX());
		float b = Math.abs(e.getPosition().y) - Math.abs(getTILE().getTileRect().centerY());
		a = a*a;
		b = b*b;
		float c = (float) Math.sqrt(a + b) - e.getBitmap().getWidth();	
		return (c<=effectRadius) ? true: false;	
	}
	
	/**
	 * Shoot the target.
	 */
	private void shoot() {
		// Shoot Target;		
		if((System.currentTimeMillis() - lastMissileTime) >= missileDelay){		
			Vector2D missilePosition = new Vector2D(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY());
			missiles.add(new Missile(missilePosition));
			lastMissileTime = System.currentTimeMillis();
		}	
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		if(getGameState() == State.BUILD){
			canvas.drawCircle(this.getTILE().getTileRect().centerX(), this.getTILE().getTileRect().centerY(), effectRadius, PAINT);
			canvas.rotate(faceAngle, (getTILE().x + (getBitmap().getWidth() / 2)), (getTILE().y + (getBitmap().getHeight() / 2)));
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
			canvas.restore();
		}
		else{
			canvas.rotate(faceAngle, (getTILE().x + (getBitmap().getWidth() / 2)), (getTILE().y + (getBitmap().getHeight() / 2)));
			canvas.drawBitmap(getBitmap(), getTILE().x, getTILE().y, null);
			canvas.restore();
		}
		super.draw(canvas);
		for(Missile m: missiles){
			m.draw(canvas);
		}
	}

	/**
	 * Return the angle which the enemy is at.
	 * @return
	 */
	private float targetAngle(){
		float x = currentTarget.getPosition().x - getRect().centerX();
		float y = -(currentTarget.getPosition().y - getRect().centerY());
		double rads = Math.atan2(y, x);
		if(rads < 0) rads = Math.abs(rads);
		else rads = 2 * Math.PI - rads;
		return (float) Math.toDegrees(rads);
	}
	
	/**
	 * The static class represents a missile.
	 * @author Imran Adan 
	 *
	 */
	private static class Missile implements Updateable, Renderable{
		
		private static final int MIN_X_LOCATION = 0;
		private static final int MIN_Y_LOCATION = 0;
		private static final int MAX_X_LOCATION = GameResources.getDrawingPanelWidth();
		private static final int MAX_Y_LOCATION = GameResources.getDrawingPanelHeight();
		
		private static final int INIT_MISSILE_DAMAGE = 150;
		private static final float MISSILE_SPEED = 2f;
		private static final int MISSILE_WIDTH = RENDERABLE_WIDTH/2;
		private static final int MISSILE_HEIGHT;
		private static final Bitmap BITMAP;
		private static final Paint PAINT;
		
		/**
		 * Define class constants.
		 */
		static{
			PAINT = PaintBank.ENEMY_SPWANS.getPaint();
			Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.missile);
			MISSILE_HEIGHT =  b.getHeight();
			BITMAP = Bitmap.createScaledBitmap(b, MISSILE_WIDTH, MISSILE_HEIGHT, true);
		}
		
		private Vector2D position;
		private Vector2D velocity;
		private boolean missileDestroyed;
		private Bitmap bitmap;
		private float faceAngle;
		private int damage;
		private Rect rect;
		private boolean fired;
		
		/**
		 * Create a new missile, with a given position vector.
		 * @param p
		 */
		public Missile(Vector2D p){
			missileDestroyed = false;
			position = p;
			velocity = new Vector2D(0, 0);
			faceAngle = 0f;
			bitmap = BITMAP;
			int left = (int) position.x;
			int top = (int) position.y;
			int right = left + MISSILE_WIDTH;
			int bottom =  top + MISSILE_HEIGHT;
			damage = Missile.INIT_MISSILE_DAMAGE;
			rect = new Rect(left, top, right, bottom);
			fired = false;
		}
		
		@Override
		public <T> void update(T... properties) {
			if(properties.length == 0){
				move();
				return;
			}
			
			Enemy e = (Enemy) properties[0];
			velocity = (Vector2D.subtract(e.getPosition(), position));			
			Vector2D.normalise(velocity);

			faceAngle = targetAngle(e);
			if (rect.intersect(e.getRect())) {
				e.setHealth(e.getHealth() - damage);
				if (e.getHealth() <= 0) {
					e.setDead(true);
				}
				missileDestroyed = true;
			} 
			
			move();
		}

		@Override
		public void draw(Canvas canvas) {
			if(!fired)
			{
				GameController.getController().playSFX(SoundPlayer.SFX.SOUND_ROCKET);
				fired = true;
			}
			canvas.save();
			canvas.rotate(faceAngle, (position.x + (bitmap.getWidth() / 2)), (position.y + (bitmap.getHeight() / 2)));
			canvas.drawBitmap(bitmap, position.x, position.y, null);
			canvas.restore();
			
		}
		
		private void move() {	
			position.x = position.x + (velocity.x * MISSILE_SPEED);
			position.y = position.y + (velocity.y * MISSILE_SPEED);
			
			int left = (int) position.x;
			int top = (int) position.y;
			int right =  left + MISSILE_WIDTH;
			int bottom = top + MISSILE_HEIGHT;	
			rect.set(left, top, right, bottom);
			
			if(position.x > MAX_X_LOCATION || position.y > MAX_Y_LOCATION || position.x < MIN_X_LOCATION || position.y < MIN_Y_LOCATION){
				missileDestroyed = true;
				Log.d(TAG, "BULLET OUR OF RANGE GETT DELETED!!!!!!");
			}
			
		}
		
		/**
		 * Return the angle which the enemy is at.
		 * @return
		 */
		private float targetAngle(Enemy e) {
			float x = e.getPosition().x - rect.centerX();
			float y = -(e.getPosition().y - rect.centerY());
			double rads = Math.atan2(y, x);
			if (rads < 0)
				rads = Math.abs(rads);
			else
				rads = 2 * Math.PI - rads;
			return (float) Math.toDegrees(rads);
		}
	}
}
