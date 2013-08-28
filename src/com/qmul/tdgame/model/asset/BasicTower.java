/**
 * 
 */
package com.qmul.tdgame.model.asset;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.util.DeviceResources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Basic Tower class represents an The initial type of tower the player starts
 * off with.
 * 
 * @author imran
 * 
 */
public class BasicTower extends Tower {

	private static final String TAG = BasicTower.class.getSimpleName();
	/**
	 * Initial Health of all Basic Tower objects
	 */
	
	public static final int INITIAL_STARING_PRICE;
	private static final int INITIAL_BASIC_TOWER_HEALTH;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;

	/**
	 * Initialise the classes constants
	 */
	static {
		INITIAL_STARING_PRICE = 1500;
		INITIAL_BASIC_TOWER_HEALTH = 500;
		Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.com_tower);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		PAINT = new Paint();
		PAINT.setColor(Color.BLUE);
		PAINT.setStyle(Paint.Style.FILL);
	}

	/**
	 * Create a new basic tower at a Tile.
	 * 
	 * @param t
	 *            The tile.
	 */
	public BasicTower(Tile t) {
		super(t, BITMAP, PAINT, INITIAL_BASIC_TOWER_HEALTH);
		setPrice(INITIAL_STARING_PRICE);
		setDamageDelay(1000);
		setRect(new Rect(getTILE().x, getTILE().y, getTILE().x + RENDERABLE_WIDTH, getTILE().y + RENDERABLE_HEIGHT));
	}

	@Override
	public <T> void update(T... properties) {
		if(GameController.getController().getGame().isWaveStarted()){
			checkTowerCollision();
			updateHealthBar();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(getBitmap(), this.getTILE().x, this.getTILE().y, null);
		super.draw(canvas);
	}

	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}

}
