/**
 * 
 */
package com.qmul.tdgame.util;

import android.content.res.Resources;

/**
 * DeviceResources hold all the values that are required by the game about the
 * device it's currentley running.
 * 
 * @author Imran
 */
public final class DeviceResources {

	
	private static Resources resources;
	private static int screenWidth;
	private static int screenHeight;
	
	public static int longSide = 0;


	/**
	 * Set the screen size of the device.
	 * 
	 * @param screenWidth
	 */
	public static void setScreenWidth(int screenWidth) {
		DeviceResources.screenWidth = screenWidth;
	}

	/**
	 * Set the screen height of the device.
	 * 
	 * @param screenHeight
	 */
	public static void setScreenHeight(int screenHeight) {
		DeviceResources.screenHeight = screenHeight;
	}

	/**
	 * Get the screen width.
	 * 
	 * @return
	 */
	public static int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Get the Screen
	 * 
	 * @return
	 */
	public static int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Get an instance of the applications resources.
	 * 
	 * @return
	 */
	public static Resources getResources() {
		return resources;
	}

	/**
	 * Set the instance of the applications resources.
	 * 
	 * @param resources
	 *            The applications resources.
	 */
	public static void setResources(Resources resources) {
		DeviceResources.resources = resources;
	}

}
