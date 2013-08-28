/**
 * 
 */
package com.qmul.tdgame.util;

import java.util.List;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.asset.Game;
import com.qmul.tdgame.model.asset.Tower;
import com.qmul.tdgame.model.core.Tile;

/**
 * Class that has utiliy operations.
 * @author Imran
 *
 */
public final class UtilOps {
		
	/**
	 * Search for the nearest target. This method looks for
	 * the tower closest to the current enemy's position. It use 
	 * Manhattan distance to determine this. This method also 
	 * has a low probability to return the most furthest target.
	 */
	public static Tower nearestTowerFrom(Tile src) {
		List<Tower> player_towers = GameController.getController().getGame().getPlayerTowers();
		Tower closest = player_towers.get(0);
		for(int i = 1; i<player_towers.size(); i++){
			Tower current = player_towers.get(0);
			if(manhattanDistanceBetween(src, current.getTILE()) > manhattanDistanceBetween(src, closest.getTILE()))
				closest = current;
			
			//if(euclideanDistance(src, current.getTILE()) < euclideanDistance(src, closest.getTILE())) closest = current;
		}
		return closest;
	}


	private static int manhattanDistanceBetween(Tile src, Tile tile) {
		return Math.abs(src.x-tile.x) + Math.abs(src.y-tile.y);
	}
	
	
//    _____a^2 + b^2 = c^2_______________
//   /       2          2
// \/ (y2-y1)  + (x2-x1)
// 
	private static int euclideanDistance(Tile src, Tile dst){
		return (int) Math.sqrt(Math.pow(Math.abs(dst.y - src.y), 2.0) + Math.pow(Math.abs(dst.x-src.x), 2.0));
	}
}
