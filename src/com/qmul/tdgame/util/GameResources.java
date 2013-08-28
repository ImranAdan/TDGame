/**
 * 
 */
package com.qmul.tdgame.util;

import com.qmul.tdgame.model.core.Tile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Game Resources is a utlity class that holds additional Information required to create 
 * a game instance
 * @author Imran
 *
 */
public final class GameResources {
	
	public static Context currentContext;
	
	public static boolean playerStartedGame = false;
	private static int drawingPanelWidth;
	private static int drawingPanelHeight;
	
	/**
	 * @return the drawingPanelWidth
	 */
	public static final int getDrawingPanelWidth() {
		return drawingPanelWidth;
	}

	/**
	 * @return the drawingPanelHeight
	 */
	public static final int getDrawingPanelHeight() {
		return drawingPanelHeight;
	}

	/**
	 * @param drawingPanelWidth
	 *            the drawingPanelWidth to set
	 */
	public static final void setDrawingPanelWidth(int drawingPanelWidth) {
		GameResources.drawingPanelWidth = drawingPanelWidth;
	}

	/**
	 * @param drawingPanelHeight
	 *            the drawingPanelHeight to set
	 */
	public static final void setDrawingPanelHeight(int drawingPanelHeight) {
		GameResources.drawingPanelHeight = drawingPanelHeight;
	}
	
	
	public static Bitmap rotate(Bitmap b, double degrees) {
	        Matrix m = new Matrix();
	        m.setRotate((float) degrees, b.getWidth()/2, b.getHeight()/2);
	        Bitmap b2 = Bitmap.createBitmap(b, 0,0, Tile.TILE_WIDTH, b.getHeight(), m, true);
	        b.recycle();
	        return b2;
	}
}
