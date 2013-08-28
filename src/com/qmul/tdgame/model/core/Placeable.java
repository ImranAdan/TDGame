/**
 * 
 */
package com.qmul.tdgame.model.core;

import android.util.Log;

import com.qmul.tdgame.model.asset.GameMap;
import com.qmul.tdgame.model.asset.Shield;
import com.qmul.tdgame.model.asset.Tower;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;

/**
 * A Placeable object is any object that can be placed in the world Map.
 * @author Imran
 *
 */
public abstract class Placeable implements Updateable, Renderable {

	private static final String TAG = Placeable.class.getSimpleName();
	private final Tile TILE;

	/**
	 * Create a new placeable object at the specified Tile. 
	 */
	public Placeable(Tile T) {
		this.TILE = T;
		if(this instanceof Shield){
			this.TILE.setOccupiedBy('T');
		}else{
			this.TILE.setOccupied(true);
			if(this instanceof Tower){
				this.TILE.setOccupiedBy('T');
			}else{
				this.TILE.setOccupiedBy('I');
			}
		}
	}

	public static String getTag() {
		return TAG;
	}

	/**
	 * Free the tile, making it unoccupied and empty.
	 */
	public void free(){
		if(this instanceof Shield){
			this.TILE.setOccupied(true);
			this.TILE.setOccupiedBy('T');
		}else{
			this.TILE.setOccupied(false);
			this.TILE.setOccupiedBy('E');
		}
	}
	
	/**
	 * Return the Tile in which this placeable object resides.
	 * @return
	 */
	public Tile getTILE() {
		return TILE;
	}

}
