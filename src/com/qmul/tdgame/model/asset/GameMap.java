/**
 * 
 */
package com.qmul.tdgame.model.asset;

import android.graphics.Canvas;
import android.util.Log;

import com.qmul.tdgame.model.core.Grid2D;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;

/**
 * This class represents the game map. This class implements singleton pattern
 * this means that the same instance will always be returned.
 * 
 * @author imran
 * 
 */
public class GameMap implements Updateable, Renderable {

	private static final String TAG = GameMap.class.getSimpleName();
	private final Grid2D game2DGrid;
	private State mapState;

	public GameMap(int w, int h) {
		game2DGrid = new Grid2D(w, h);
		mapState = State.WAVE;
	}


	/**
	 * TODO The game map can only be updated with the following: Either a Tile
	 * is occupied or free. three Properties are passed X, Y and a boolean
	 * indication if that tile should be set to true or false, in terms of
	 * occupation.
	 */
	@Override
	public <T> void update(T... properties) {
		switch(mapState){
			case WAVE: 
				applyWavePaint();
				break;
			case BUILD: 
				applyBuildPaint(); 
				break;
			case SELL:
				applySellPaint();
				break;
			case UPGRADE:
				applyBuildPaint();
				break;
			default:
				applyWavePaint();
				break;
		}
	}

	/**
	 * Apply the paint to be drawn when the sell button 
	 * is pressed.
	 */
	private void applySellPaint() {
		applyBuildPaint();
	}

	/**
	 * Apply the paint to be drawn when the wave button 
	 * is pressed.
	 */
	private void applyWavePaint() {
		for (int x = 0; x < game2DGrid.getRows(); x++) {
			for (int y = 0; y < game2DGrid.getCols(); y++) {
				game2DGrid.getTILES()[x][y].setPaint(PaintBank.NORMAL.getPaint());
			}
		}	
	}

	/**
	 * Apply the paint to be drawn when the build button 
	 * is pressed.
	 */
	private void applyBuildPaint() {
		for (int x = 0; x < game2DGrid.getRows(); x++) {
			for (int y = 0; y < game2DGrid.getCols(); y++) {
				if(x == 0){
					game2DGrid.getTILES()[x][y].setPaint(PaintBank.ENEMY_SPWANS.getPaint());
				}
				else if (game2DGrid.getTILES()[x][y].isOccupied()) {
					game2DGrid.getTILES()[x][y].setPaint(PaintBank.UNAVAILABLE.getPaint());
				} else {
					game2DGrid.getTILES()[x][y].setPaint(PaintBank.AVAILABLE.getPaint());
				}
			}
		}
	}


	@Override
	public void draw(Canvas canvas) {
		switch (mapState) {
		case WAVE:
			renderWaveMap(canvas);
			break;
		case BUILD:
			renderBuildMap(canvas);
			break;
		case SELL:
			renderSellMap(canvas);
			break;
		case UPGRADE:
			renderBuildMap(canvas);
			break;
		default:
			renderWaveMap(canvas);
			break;
		}
	}

	/**
	 * Requires further work to make it more visible.
	 * @param canvas
	 */
	private void renderSellMap(Canvas canvas) {
		for (int x = 0; x < game2DGrid.getRows(); x++) {
			for (int y = 0; y < game2DGrid.getCols(); y++) {
				canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),game2DGrid.getTILES()[x][y].getPaint());
				if(game2DGrid.getTILES()[x][y].getOccupiedBy() == 'T' || game2DGrid.getTILES()[x][y].getOccupiedBy() == 'I'){
					canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),PaintBank.SELL.getPaint());
				}
				else{
					canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),Tile.getBorderPaint());
				}
			}
		}
	}


	/**
	 * Render the maps at the build state to the canvas.
	 * @param canvas
	 */
	private void renderBuildMap(Canvas canvas) {
		for (int x = 0; x < game2DGrid.getRows(); x++) { 
			for (int y = 0; y < game2DGrid.getCols(); y++) {
					canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),game2DGrid.getTILES()[x][y].getPaint());
					canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),Tile.getBorderPaint());	
			}
		}
	}


	/**
	 * Render the maps at the waves state to the canvas.
	 * @param canvas
	 */
	private void renderWaveMap(Canvas canvas) {
		for (int x = 0; x < game2DGrid.getRows(); x++) { 
			for (int y = 0; y < game2DGrid.getCols(); y++) {
					canvas.drawRect(game2DGrid.getTILES()[x][y].getTileRect(),game2DGrid.getTILES()[x][y].getPaint());	
			}
		}
	}


	/**
	 * Calculate the number if items or towers.
	 * @param c T or E or I
	 * @return The a total number.
	 */
	public int getNumberOf(char c) {
		int total = 0;
		for (int x = 0; x < game2DGrid.getRows(); x++) {
			for (int y = 0; y < game2DGrid.getCols(); y++) {
				if(game2DGrid.getTILES()[x][y].getOccupiedBy()==c)
					total++;
			}
		}
		return total;
	}
	
	
	

	/**
	 * @return the mapState
	 */
	public State getMapState() {
		return mapState;
	}

	/**
	 * @param mapState
	 *            the mapState to set
	 */
	public void setMapState(State mapState) {
		this.mapState = mapState;
	}

	/**
	 * Get the tag, used for debugging.
	 * 
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}


	/**
	 * Get the underlying grid structure.
	 * 
	 * @return the game2DGrid
	 */
	public Grid2D getGame2DGrid() {
		return game2DGrid;
	}


	/**
	 * Set each parent of the the tiles to null so 
	 * that path finding can happen.
	 */
	public void setParentsToNull() {
		for (int x = 0; x < game2DGrid.getRows(); x++) {
			for (int y = 0; y < game2DGrid.getCols(); y++) {
				game2DGrid.getTILES()[x][y].setParentTile(null);
			}
		}
	}
}
