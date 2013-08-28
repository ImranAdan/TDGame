/**
 * 
 */
package com.qmul.tdgame.model.core;


import java.util.LinkedList;
import java.util.List;

import android.util.Log;



/**
 * Gird2D is the underlying class that forms the game's grid. All moving objects make
 * use of Grid2D to navigate through the games map.
 * @author imran
 * 
 */
public class Grid2D {

	private static final String TAG = Grid2D.class.getSimpleName();

	/*
	 * Width and height of the Grid.
	 */
	private final int w, h;

	/*
	 * The 2D Grid
	 */
	private final Tile[][] TILES;
	private int rows, cols;

	/**
	 * 
	 */
	public Grid2D(int w, int h) {
		this.w = w;
		this.h = h;
		rows = w / Tile.TILE_WIDTH;
		cols = h / Tile.TILE_HEIGHT;
		TILES = new Tile[rows][cols];
		setUpComponents();
		setUpAdjacencies();
	}

	/**
	 * Get a tile at the designated x,y coordinates.
	 * @param x X
	 * @param y Y
	 * @return Tile at x y.
	 */
	public Tile getTileAt(float x, float y){
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if ((TILES[i][j].getX() <= x && TILES[i][j].getEndx() > x)
						&& (TILES[i][j].getY() <= y && TILES[i][j].getEndy() > y))
					return TILES[i][j];
			}
		}
		return null;
	}

	/**
	 * Set up all Adjacent neighbours for all Tiles in the 
	 * this Grid object.
	 */
	private void setUpAdjacencies(){
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				List<Tile> neighbours = new LinkedList<Tile>();
				List<Tile> fourNeighbours = new LinkedList<Tile>();
				// EAST 
				if (j + 1 < cols){ neighbours.add(TILES[i][j + 1]); fourNeighbours.add(TILES[i][j + 1]); }
				// SOUTH EAST
				if (i + 1 < rows && j + 1 < cols){ neighbours.add(TILES[i + 1][j + 1]); }
				// SOUTH 
				if (i + 1 < rows){ neighbours.add(TILES[i + 1][j]); fourNeighbours.add(TILES[i + 1][j]); }
				// SOUTH WEST
				if (i + 1 < rows && j - 1 >= 0){ neighbours.add(TILES[i + 1][j - 1]); }
				// WEST
				if (j - 1 >= 0){ neighbours.add(TILES[i][j - 1]); fourNeighbours.add(TILES[i][j - 1]); }
				// NORTH WEST
				if (i - 1 >= 0 && j - 1 >= 0){ neighbours.add(TILES[i - 1][j - 1]); }
				// NORTH
				if (i - 1 >= 0){ neighbours.add(TILES[i - 1][j]); fourNeighbours.add(TILES[i - 1][j]);}
				// NORTH EAST
				if (i - 1 >= 0 && j + 1 < cols){ neighbours.add(TILES[i - 1][j + 1]);}
				TILES[i][j].setEightNeighbours(neighbours);
				TILES[i][j].setFourNeighbours(fourNeighbours);
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Grid2D [w=" + w + ", h=" + h + ", rows=" + rows + ", cols="
				+ cols +  "]";
	}
	
	/**
	 * @return the Width of the gird.
	 */
	public int getW() {
		return w;
	}

	/**
	 * @return the height of the grid.
	 */
	public int getH() {
		return h;
	}

	/**
	 * @return the tILES
	 */
	public Tile[][] getTILES() {
		return TILES;
	}

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return the cols
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @param cols the cols to set
	 */
	public void setCols(int cols) {
		this.cols = cols;
	}
	

	private void setUpComponents() {
		int i = 0;
		int j = 0;
		for (int x = 0; i < rows; x += Tile.TILE_WIDTH) {
			for (int y = 0; j < cols; y += Tile.TILE_HEIGHT) {
				TILES[i][j] = new Tile(x, y);
				j++;
			}
			j = 0;
			i++;
		}
	}

	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}
	
	
	public boolean outOfBounds(float x, float y){
		return x > w - TILES[0][cols-1].endx || y > h - TILES[rows-1][0].endy || x < 0 || y < 0;
	}

}
