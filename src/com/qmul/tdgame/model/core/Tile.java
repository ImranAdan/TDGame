/**
 * 
 */
package com.qmul.tdgame.model.core;

import java.util.List;

import com.qmul.tdgame.model.enums.PaintBank;
import com.qmul.tdgame.model.interfaces.Renderable;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;



/**
 * The tile represents a single sqaure in the game map.
 * @author imran
 *
 */
public final class Tile {

	public static final int TILE_WIDTH = 50;
	public static final int TILE_HEIGHT= 50;
	private static final String TAG = Tile.class.getSimpleName();
	private static final Paint BORDER_PAINT = PaintBank.BORDER.getPaint();
	

	/*
	 * Geometric fields 
	 */
	public final int x, y, endx, endy;
	
	/*
	 * Walkable boolean
	 */
	private boolean occupied;
	
	/*
	 * A* Search Parameters
	 */
	private Tile parentTile;
	private int gCost;
	private int hCost;
	private int fCost;
	
	/*
	 * Get the Adjacent Neighbours
	 */
	private List<Tile> eightNeighbours;
	private List<Tile> fourNeighbours;
	
	/*
	 * Overlaying Rectangle used for drawing purposes.
	 */
	private final Rect tileRect;
	private Paint inner_paint;
	
	private char occupiedBy;

	/**
	 * Create a new Tile at x and y positions
	 * @param x Initial X
	 * @param y Initial Y
	 */
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		occupied = false;
		occupiedBy = 'E';
		endx = x + TILE_WIDTH;	
		endy = y + TILE_HEIGHT;
		tileRect = new Rect(this.x, this.y, endx, endy);
		gCost = 10;
		hCost = 0;
		fCost = 0;
	}

	/**
	 * Get the X position of this Tile. 
	 * The X position also refers to left.
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y position of this Tile. 
	 * The Y position also refers to Top.
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the width of the tile. Which is 
	 * x + {@link Renderable#RENDERABLE_WIDTH}.
	 * This method also returns right. 
	 * @return the endx
	 */
	public int getEndx() {
		return endx;
	}

	/**
	 * Get the height of the tile. Which is 
	 * y + {@link Renderable#RENDERABLE_HEIGHT}.
	 * This method also returns bottom. 
	 * @return the endy
	 */
	public int getEndy() {
		return endy;
	}

	/**
	 * Boolean that indicates whether a tile is walkable or not.
	 * @return the occupied
	 */
	public boolean isOccupied() {
		return occupied;
	}

	/**
	 * Return the parent tile of this tile. 
	 * This is to be used during path reconstruction.
	 * @return the parentTile
	 */
	public Tile getParentTile() {
		return parentTile;
	}

	/**
	 * Get the G Cost. Move cost.
	 * @return the gCost
	 */
	public int getgCost() {
		return gCost;
	}

	/**
	 * Get the H cost. Heurstic Cost 
	 * @return the hCost
	 */
	public int gethCost() {
		return hCost;
	}

	/**
	 * Get the F cost. G+H
	 * @return the fCost
	 */
	public int getfCost() {
		return fCost;
	}

	/**
	 * Get the eight neighbours
	 * @return the eightNeighbours
	 */
	public List<Tile> getEightNeighbours() {
		return eightNeighbours;
	}

	/**
	 * @return the fourNeighbours
	 */
	public List<Tile> getFourNeighbours() {
		return fourNeighbours;
	}

	/**
	 * @return the tileRect
	 */
	public Rect getTileRect() {
		return tileRect;
	}

	/**
	 * @param occupied the occupied to set
	 */
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	/**
	 * @param parentTile the parentTile to set
	 */
	public void setParentTile(Tile parentTile) {
		this.parentTile = parentTile;
	}

	/**
	 * @param gCost the gCost to set
	 */
	public void setgCost(int gCost) {
		this.gCost = gCost;
	}

	/**
	 * @param hCost the hCost to set
	 */
	public void sethCost(int hCost) {
		this.hCost = hCost;
	}

	/**
	 * @param fCost the fCost to set
	 */
	public void setfCost(int fCost) {
		this.fCost = fCost;
	}

	/**
	 * @param eightNeighbours the eightNeighbours to set
	 */
	public void setEightNeighbours(List<Tile> eightNeighbours) {
		this.eightNeighbours = eightNeighbours;
	}

	/**
	 * @param fourNeighbours the fourNeighbours to set
	 */
	public void setFourNeighbours(List<Tile> fourNeighbours) {
		this.fourNeighbours = fourNeighbours;
	}
	
	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}

	/**
	 * @return the paint
	 */
	public void setPaint(Paint p) {
		this.inner_paint = p;
	}
	
	/**
	 * @return the paint
	 */
	public Paint getPaint() {
		return inner_paint;
	}
	
	/**
	 * @return the occupiedBy
	 */
	public char getOccupiedBy() {
		return occupiedBy;
	}

	/**
	 * @param occupiedBy the occupiedBy to set
	 */
	public void setOccupiedBy(char occupiedBy) {
		this.occupiedBy = occupiedBy;
	}

	public static Paint getBorderPaint() {
		return BORDER_PAINT;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tile [x=" + x + ", y=" + y + ", endx=" + endx + ", endy="
				+ endy + ", occupied=" + occupied + ", occOccupiedBy="+ occupiedBy + ", hasParentTile="
				+ (parentTile!=null) + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		Tile t = (Tile) o;
		if(this.x == t.x && this.y == t.y) // Equals is being used in the wrong way, but have to.
			return true;
		return false;
	}
	
	
}
