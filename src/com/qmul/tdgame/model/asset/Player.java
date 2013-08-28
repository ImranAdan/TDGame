/**
 * 
 */
package com.qmul.tdgame.model.asset;

import com.qmul.tdgame.model.interfaces.Updateable;

/**
 * Class represents a player object. The player object contains score and name of the player.
 * @author imran
 *
 */
public final class Player{
	
	private static final int INITIAL_CASH = 10000;
	
	private final String PLAYER_NAME;
	private int cash;

	/**
	 * Create a new player passing his/her name. 
	 * The player object has an initial score of 0.
	 * @param playerName
	 */
	public Player(String playerName) {
		PLAYER_NAME = playerName;
		cash = INITIAL_CASH;
	}
	
	/**
	 * Get the current score of the player.
	 * @return Players Score.
	 */
	public int getCash() {
		return cash;
	}

	/**
	 * Set the players current score.
	 * @param cash The score added to the player.
	 */
	public void setCash(int cash) {
		this.cash = cash;
	}

	/**
	 * Get the players name.
	 * @return Name of the player.
	 */
	public String getPLAYER_NAME() {
		return PLAYER_NAME;
	}

}
