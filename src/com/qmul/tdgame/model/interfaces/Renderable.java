/**
 * 
 */
package com.qmul.tdgame.model.interfaces;

import android.graphics.Canvas;

/**
 * Interface that is to be implemented by all game assets that 
 * are to be drawn on the screen.
 * @author imran
 */
public interface Renderable {
	
	/**
	 * Width of the drawable resource.
	 */
	public static final int RENDERABLE_WIDTH = 50;
	/**
	 * Height of the drawable resource.
	 */
	public static final int RENDERABLE_HEIGHT = 50;
	
	/**
	 * Draw the resources to a canavs.
	 * @param canvas The canvas to be drawn on.
	 */
	public void draw(Canvas canvas);
}
