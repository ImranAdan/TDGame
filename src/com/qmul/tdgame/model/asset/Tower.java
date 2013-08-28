/**
 * 
 */
package com.qmul.tdgame.model.asset;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Placeable;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.util.RandomGenerator;

/**
 * This class represents a tower object. A Tower object can have two different
 * types of implementations. A {@link BasicTower} and a {@link AdvancedTower}.
 * 
 * @author imran
 * 
 */
public abstract class Tower extends Placeable {

	private static final String TAG = Tower.class.getSimpleName();
	private final Class<?> TYPE;
	private final Paint PAINT;
	private Bitmap BITMAP;
	private final Vector2D p;

	private Shield currentShield;

	private Rect rect;
	private int price;
	private int health;

	private long damageDelay;
	private long lastDamageRecieved;

	private boolean dead;
	private State gameState;
	private boolean upgraded;
	private int startingHealth;
	private Rect healthBar;

	/**
	 * Create a new Tower.
	 * 
	 * @param tile
	 *            The Tile to be created at.
	 * @param bitmap
	 *            The Image of the Tower.
	 * @param paint
	 *            The Paint object.
	 * @param health
	 *            Initial Tower Health
	 */
	public Tower(Tile tile, Bitmap bitmap, Paint paint, int health) {
		super(tile);
		TYPE = this.getClass();
		BITMAP = bitmap;
		p = new Vector2D(tile.x, tile.y);
		PAINT = paint;
		this.health = health;
		setDead(false);
		this.getTILE().setOccupiedBy('T');
		upgraded = false;

		startingHealth = health;
		int left = (int) getTILE().x;
		int right = left + RENDERABLE_WIDTH - 2;
		int top = (int) getTILE().y - 12;
		int bottom = top + 3;

		healthBar = new Rect(left, top, right, bottom);
	}

	/**
	 * Get the current Health of the Tower object.
	 * 
	 * @return
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Set the health of the current Tower
	 * 
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Get the type of the Tower.
	 * 
	 * @return the type
	 */
	public Class<?> getType() {
		return TYPE;
	}

	/**
	 * Return the tag of this class for debugging only.
	 * 
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}

	/**
	 * Get the Paint
	 * 
	 * @return the paint
	 */
	public Paint getPaint() {
		return PAINT;
	}

	/**
	 * Get the Bitmap
	 * 
	 * @return the bitmap
	 */
	public Bitmap getBitmap() {
		return BITMAP;
	}

	/**
	 * @return the p
	 */
	public Vector2D getVector() {
		return p;
	}

	/**
	 * Spawn an initial Set of towers.
	 * 
	 * @return A List of Towers.
	 */
	public static List<Tower> spwanInitialSet() {
		int n_Towers = 2; // RandomGenerator.generateRandom(5);
		List<Tower> initialSet = new CopyOnWriteArrayList<Tower>();
		for (int i = 1; i <= n_Towers; i++)
			initialSet.add(Tower.spawanAtRandomLocation());
		return initialSet;
	}

	/**
	 * Spawn Tower at a controlled random location.
	 * 
	 * @return A target a
	 */
	private static Tower spawanAtRandomLocation() {
		int rowNum = RandomGenerator.pickRandomTRow(GameController
				.getController().getGame().getMap().getGame2DGrid().getRows());
		int colNum = RandomGenerator.pickRandomTCol(GameController
				.getController().getGame().getMap().getGame2DGrid().getCols());
		Tile T = GameController.getController().getGame().getMap()
				.getGame2DGrid().getTILES()[rowNum][colNum];
		return new BasicTower(T);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tower [TYPE=" + TYPE.getSimpleName() + ", health=" + health
				+ ", At Tile=" + getTILE().toString() + "]";
	}

	public void setState(State gameState) {
		this.gameState = gameState;
	}

	public State getGameState() {
		return gameState;
	}

	/**
	 * @return the dead
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * @return the rect
	 */
	public final Rect getRect() {
		return rect;
	}

	/**
	 * @param rect
	 *            the rect to set
	 */
	public final void setRect(Rect rect) {
		this.rect = rect;
	}

	/**
	 * @param dead
	 *            the dead to set
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * @return the damageDelay
	 */
	public final long getDamageDelay() {
		return damageDelay;
	}

	/**
	 * @return the lastDamageRecieved
	 */
	public final long getLastDamageRecieved() {
		return lastDamageRecieved;
	}

	/**
	 * @param damageDelay
	 *            the damageDelay to set
	 */
	public final void setDamageDelay(long damageDelay) {
		this.damageDelay = damageDelay;
	}

	/**
	 * @param lastDamageRecieved
	 *            the lastDamageRecieved to set
	 */
	public final void setLastDamageRecieved(long lastDamageRecieved) {
		this.lastDamageRecieved = lastDamageRecieved;
	}

	/**
	 * Check collision for this specific tower.
	 */
	public void checkTowerCollision() {
		for (Enemy e : GameController.getController().getGame()
				.getCurrentWave().getEnemies()) {
			if (e.getRect().intersect(getRect())) {
				if ((System.currentTimeMillis() - lastDamageRecieved) >= damageDelay) {
					if (protectedByShield()) {
					} else {
						setHealth(getHealth() - e.getDamage());
						lastDamageRecieved = System.currentTimeMillis();
					}
				}
				e.setDead(true);
			}
		}
	}

	/**
	 * Indicate whether this tower is protected a shield.
	 * 
	 * @return
	 */
	public boolean protectedByShield() {
		List<Item> playerItems = GameController.getController().getGame()
				.getPlayerItems();
		for (Item item : playerItems) {
			if (item instanceof Shield && item.getTILE().x == this.getTILE().x
					&& item.getTILE().y == this.getTILE().y) {
				currentShield = (Shield) item;
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the currentShield
	 */
	public final Shield getCurrentShield() {
		return currentShield;
	}

	/**
	 * @param currentShield
	 *            the currentShield to set
	 */
	public final void setCurrentShield(Shield currentShield) {
		this.currentShield = currentShield;
	}

	public boolean upgraded() {
		return upgraded;
	}

	public void upgrade() {
		damageDelay += 200;
		price += price;
		health += health;
		upgraded = true;
	}

	/**
	 * Update the health bars of each enemy.
	 */
	protected void updateHealthBar() {

		float healthState = (float) health / (float) startingHealth;
		Log.d(TAG, "STARTING = " + startingHealth);
		Log.d(TAG, "CURRENT HEALTH = " + health);
		Log.d(TAG, "HEALTH STATE = " + healthState);

		if (healthState < 0.25f) {
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;

			int right = left + (RENDERABLE_WIDTH / 4);

			healthBar.set(left, top, right, bottom);
		} else if (healthState < 0.75f) {
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;

			int right = left + (RENDERABLE_WIDTH / 2);
			healthBar.set(left, top, right, bottom);
		} else {
			int left = (int) getTILE().x;
			int top = (int) getTILE().y - 5;
			int bottom = top + 3;

			int right = left + (RENDERABLE_WIDTH - 10);
			healthBar.set(left, top, right, bottom);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		float healthState = (float) health / (float) startingHealth;
		if (healthState < 0.25f) {
			canvas.drawRect(healthBar, PaintBank.LOW_HEALTH.getPaint());
		} else if (healthState < 0.75f) {
			canvas.drawRect(healthBar, PaintBank.MIDDLE_HEALTH.getPaint());
		} else {
			canvas.drawRect(healthBar, PaintBank.GOOD_HEALTH.getPaint());
		}
	}
}
