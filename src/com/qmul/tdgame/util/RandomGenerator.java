package com.qmul.tdgame.util;

import java.util.Random;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.asset.GameMap;
import com.qmul.tdgame.model.core.Tile;

/**
 * Utility class that generates various random numbers that are in context to the application.
 * @author Imran
 *
 */
public final class RandomGenerator {
	
	
	/**
	 * 
	 * Generate a random number within a max and min range using the following formula:
	 * RAND.nextInt(max - min + 1) + min;
	 */
	
	private static final Random RAND = new Random();
	
		
	/**
	 * Generates a Random Cell Location on the Grid2D.
	 * @param minRange
	 * @param maxRange
	 * @return Random Cell Location
	 */
	public static int generateCellLocation(int minRange, int maxRange){
		return (int) (minRange + (int)(Math.random() * ((maxRange- minRange) + 1)));
	}
		
	
	/**
	 * Get a fixed random Tower Location in a form a row number. The Row number is restricted a by a constant.
	 * Constants are defined as 0f, 0.25f, 0.5f and 1f.
	 * @param rows The total number of rows in a 2DGrid.
	 * @return A random row number restricted by a constant
	 */
	public static int pickRandomTRow(int rows){
        return (int) (RAND.nextInt((int) ((0.75*rows) - (0.25*rows) + 1)) + (0.25*rows));
	}
	
	
	/**
	 * Get a fixed random Tower Location in a form a column number. The column number is restricted a by a constant.
	 * Constants are defined as 0f, 0.25f, 0.5f and 1f.
	 * @param cols The total number of column in a 2DGrid.
	 * @return A random column number restricted by a constant
	 */
	public static int pickRandomTCol(int cols){
        return (int) (RAND.nextInt((int) ((0.75*cols) - (0.25*cols) + 1)) + (0.25*cols));
	}
	
	/**
	 * Get a fixed random Enemy Location in a form a row number. The Row number is restricted a by a constant.
	 * Constants are defined as 0f, 0.25f, 0.5f and 1f.
	 * @param rows The total number of rows in a 2DGrid.
	 * @return A random row number restricted by a constant
	 */
	public static int pickRandomERow(int rows){
//		int r = RAND.nextInt(10);
//		return (r >=5) ?  rows-1 : 0;
		return 0;
	}
	 
	/**
	 * Get a fixed random Enemy Location in a form a column number. The column number is restricted a by a constant.
	 * Constants are defined as 0f, 0.25f, 0.5f and 1f.
	 * @param cols The total number of column in a 2DGrid.
	 * @return A random column number restricted by a constant
	 */
	public static int pickRandomECol(int cols){
			return RAND.nextInt((cols-1)- 0 + 1) + 0;
	}


	/**
	 * Generate a random Number between 1 to the desired scope.
	 * @param scope The upper limit to the randomley generated numbers.
	 * @return A random number between 1 to scope.
	 */
	public static int generateRandom(int scope) {
		return RAND.nextInt(scope) + 1;
	}
	
	public static int generateRandom(int min, int max){
		return RAND.nextInt(max - min + 1) + min;
	}


	public static Tile pickRandomETile() {
		int r = pickRandomERow(GameController.getController().getGame().getMap().getGame2DGrid().getRows());
		int c = pickRandomECol(GameController.getController().getGame().getMap().getGame2DGrid().getCols());
		return GameController.getController().getGame().getMap().getGame2DGrid().getTILES()[r][c];
	}
	
}
