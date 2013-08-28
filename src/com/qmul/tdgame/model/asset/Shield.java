/**
 * 
 */
package com.qmul.tdgame.model.asset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.qmul.tdgame.R;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.util.DeviceResources;

/**
 * This class represents a shield that can be placed in top of a tower.
 * @author Imran
 * 
 */
public class Shield extends Item {

	private static final String TAG = Shield.class.getSimpleName();
	public static String getTag() { return TAG; }

	private static final int UPGRADE_PRICE = 150;
	public static final int INITIAL_STARING_PRICE;
	private static final int INITIAL_SHIELD_HEALTH;
	private static final int INITIAL_EFFECT_RADIUS;
	private static final Paint PAINT;
	private static final Bitmap BITMAP;

	/**
	 * Define class constants.
	 */
	static {
		INITIAL_STARING_PRICE = 200;
		INITIAL_SHIELD_HEALTH = 500;
		Bitmap b = BitmapFactory.decodeResource(DeviceResources.getResources(), R.drawable.shield);
		BITMAP = Bitmap.createScaledBitmap(b, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, true);
		INITIAL_EFFECT_RADIUS = 0;
		PAINT = PaintBank.EFFECT_RADIUS.getPaint();
	}

	/**
	 * Create a new shield object at a starting tile.
	 * @param T
	 * @param bitmap
	 * @param initialHealth
	 * @param effect_r
	 */
	public Shield(Tile T) {
		super(T, BITMAP, INITIAL_SHIELD_HEALTH, INITIAL_EFFECT_RADIUS);
		setPrice(INITIAL_STARING_PRICE);
		setUpgradePrice(UPGRADE_PRICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qmul.tdgame.model.interfaces.Updateable#update(T[])
	 */
	@Override
	public <T> void update(T... properties) {
		setAngle(getAngle() + 5f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qmul.tdgame.model.interfaces.Renderable#draw(android.graphics.Canvas)
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
	}
	
	
	public void upgrade(){
		super.upgrade();
	}

}
