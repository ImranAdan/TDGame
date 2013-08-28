/**
 * 
 */
package com.qmul.tdgame.model.core;

import java.util.Collections;
import java.util.LinkedList;

import com.qmul.tdgame.controller.GameController;

import android.util.Log;

/**
 * Path finder is a class used to implement  the A* path finding algorithm.
 * @author Imran
 * 
 */
public class PathFinder {

	private static final String TAG = PathFinder.class.getSimpleName();

	
	private final LinkedList<Tile> path;
	private final LinkedList<Tile> openList;
	private final LinkedList<Tile> closedList;


	/**
	 * Create a new path finder instance.
	 */
	public PathFinder(){
		path = new LinkedList<Tile>();
		openList = new LinkedList<Tile>();
		closedList = new LinkedList<Tile>();
	}

	/**
	 * Return the shortest path by using the A* algorithm.
	 * @param src The source
	 * @param dst The target 
	 * @return The path.
	 */
	public LinkedList<Tile> getShortestPath(Tile src, Tile dst) {
		Log.d(TAG, "\n\n");
		Log.d(TAG, "Starting Pathfinding");
		
		Tile start = src;
		Tile goal = dst;

		GameController.getController().getGame().getMap().setParentsToNull();
		path.clear();
		openList.clear();
		closedList.clear();
		
		openList.add(start);
		while (openList.size() > 0) {
			Tile current = openList.getFirst();
			for (Tile c : openList) {
				if (c.getfCost() < current.getfCost()) { 
					current = c;
				}
			}
			if (current.x == goal.x && current.y == goal.y) {
				return generatePath(current);
			}

			openList.remove(current);
			closedList.add(current);

			for (Tile neighbour : current.getFourNeighbours()) {
				boolean better;
				if (closedList.contains(neighbour) || neighbour.getOccupiedBy() == 'I'){
					continue;
				}

					int neighbourDistanceFromStart = getManhattanDistance(neighbour, start);
					if (!openList.contains(neighbour)) {
						openList.add(neighbour);
						better = true;
					} else if (neighbourDistanceFromStart < getManhattanDistance(current, start)) {
						better = true;
					} else {
						better = false;
					}

					if (better) {
						neighbour.setParentTile(current);
						neighbour.setgCost(neighbourDistanceFromStart);
						neighbour.sethCost(getManhattanDistance(neighbour, goal));
						neighbour.setfCost(neighbour.getgCost()+ neighbour.gethCost());
					}
			}

		}
		return null;
	}
	
	
	/**
	 * Reconstruct the path.
	 * 
	 * @param current
	 *            The last node to work backwards from to reconstruct the path.
	 */
	private LinkedList<Tile> generatePath(Tile current) {
		while (current.getParentTile() != null) {
			path.add(current);
			current = current.getParentTile();
		}
		Collections.reverse(path);
		return path;
	}

	/**
	 * Return the Manhattan Distance between two Cells.
	 * @param a First Cell
	 * @param t Target Cell
	 * @return Distance.
	 */
	private static int getManhattanDistance(Tile a, Tile t) {
		return (Math.abs(a.x - t.x) + Math.abs(a.y - t.y));
	}

	
	/**
	 * @return the tag
	 */
	public static String getTag() {
		return TAG;
	}

}
