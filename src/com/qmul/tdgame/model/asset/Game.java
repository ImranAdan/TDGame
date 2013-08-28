package com.qmul.tdgame.model.asset;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Bitmap;
import android.util.Log;

import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.State;

/**
 * The Game class represents an entire running of a game. A game object has all
 * contains all the objects to be updated and rendered to the canvas. This class
 * enforces singleton. 
 * @author Imran
 * 
 */
public class Game  {

	private static final String TAG = Game.class.getSimpleName();
	public static String getTag() { return TAG; }

	private State gameState;
	private Bitmap background;
	private GameMap map;
	
	private final Player player;
	private List<Tower> playerTowers;
	private List<Item> playerItems;


	private long releaseDelay;
	private long lastReleasedEnemyTime;
	private WaveGenerator waveGenerator;
	private Wave modelWave;
	private Wave currentWave;
	private boolean waveStarted;
	
	
	
	public Game(String playerName){
		//Game
		gameState = State.WAVE;
		background = null;	
		map = null;

		//Enemy
		releaseDelay = 1500;
		setModelWave(null);
		setWaveGenerator(new WaveGenerator());
		currentWave = null;
		waveStarted = false;
		
		// Player
		player = new Player(playerName);
		playerTowers = null;
		playerItems = new CopyOnWriteArrayList<Item>();
	}
	
	/**
	 * Setup initial parameters of the current wave.
	 * This includes adding the wave number and the total
	 * number of enemies to be included in this wave. 
	 */
	public void setUpCurrentWave() {
		Log.d("EVENTS", "Setting Up current Wave");
		setCurrentWave(new Wave(modelWave.getWaveNumber(), modelWave.getEnemyAmount()));
		currentWave.setTowerAmount(modelWave.getTowerAmount());
		currentWave.addEnemy(modelWave.getEnemies().remove(0));
		Log.d("EVENTS", currentWave.toString());
	}
	
	/**
	 * While there are enemies 
	 * left to be released, this method
	 * releases the next enemy to join the current wave.
	 */
	public void releaseNextEnemy() {
		if(currentWave.getEnemies().size() == 0){
			currentWave.addEnemy(modelWave.getEnemies().remove(0));
			return;
		}
		
		if(modelWave.getEnemies().size() > 0){
			boolean goodGap = goodGap(modelWave.getEnemies().get(0));
			if(System.currentTimeMillis() - lastReleasedEnemyTime >= releaseDelay && goodGap){
				currentWave.addEnemy(modelWave.getEnemies().remove(0));
				lastReleasedEnemyTime = System.currentTimeMillis();
			}
		}
	}
	
	/**
	 * Calculate if there is a large enough gap to release the next enemy to the
	 * current wave.
	 * 
	 * @param enemy
	 *            The enemy in front of the enemy about to be released.
	 * @return If there is a good enough gap to release the next enemy.
	 */
	private boolean goodGap(Enemy enemy) {
		Vector2D v = Vector2D.subtract(currentWave.getEnemies().get(currentWave.getEnemies().size()-1).getPosition(), enemy.getPosition());
		if(Vector2D.length(v) > 50)
			return true;
		
		return false;
	}
	

	
	/**
	 * Get the game map.
	 * @return The game map.
	 */
	public GameMap getMap() {
		return map;
	}
	
	
	/**
	 * Set the Game map.
	 * @param map The map.
	 */
	public void setMap(GameMap map) {
		this.map = map;
	}
	
	/**
	 * Set game background.
	 * @param scaledBg
	 */
	public void setBackground(Bitmap scaledBg) {
		this.background = scaledBg;
	}

	/**
	 * Get the background bitmap.
	 * @return
	 */
	public Bitmap getBackground() {
		return background;
	}
	
	/**
	 * @param gameState the gameState to set
	 */
	public void setGameState(State gameState) {
		this.gameState = gameState;
		this.map.setMapState(this.gameState);
		for(Item I: playerItems) I.setState(this.gameState);
		for(Tower T: playerTowers) T.setState(this.gameState);
	}
	
	/**
	 * @return the gameState
	 */
	public State getGameState() {
		return gameState;
	}
	
	/**
	 * @return Players Towers.
	 */
	public List<Tower> getPlayerTowers() {
		return playerTowers;
	}

	/**
	 * Set the players tower.
	 * @param towers List of towers to be added to the players towers.
	 */
	public void setPlayerTowers(List<Tower> towers) {
		this.playerTowers = towers;
	}

	/**
	 * Get the player.
	 * @return The player.
	 */
	public Player getPlayer() {
		return player;
	}


	public void setCurrentWave(Wave currentWave){
		this.currentWave = currentWave;
	}
	/**
	 * Get the current Wave.
	 * @return
	 */
	public Wave getCurrentWave(){
		return currentWave;
	}
	/**
	 * @return the playerItems
	 */
	public List<Item> getPlayerItems() {
		return playerItems;
	}
	/**
	 * @param playerItems the playerItems to set
	 */
	public void setPlayerItems(List<Item> playerItems) {
		this.playerItems = playerItems;
	}

	/**
	 * @return the modelWave
	 */
	public Wave getModelWave() {
		return modelWave;
	}
	/**
	 * @param modelWave the modelWave to set
	 */
	public void setModelWave(Wave modelWave) {
		this.modelWave = modelWave;
	}
	
	
	
	/**
	 * @return the releaseDelay
	 */
	public final long getReleaseDelay() {
		return releaseDelay;
	}
	/**
	 * @return the lastReleasedEnemyTime
	 */
	public final long getLastReleasedEnemyTime() {
		return lastReleasedEnemyTime;
	}
	/**
	 * @param releaseDelay the releaseDelay to set
	 */
	public final void setReleaseDelay(long releaseDelay) {
		this.releaseDelay = releaseDelay;
	}
	/**
	 * @param lastReleasedEnemyTime the lastReleasedEnemyTime to set
	 */
	public final void setLastReleasedEnemyTime(long lastReleasedEnemyTime) {
		this.lastReleasedEnemyTime = lastReleasedEnemyTime;
	}


	/**
	 * @return the waveStarted
	 */
	public boolean isWaveStarted() {
		return waveStarted;
	}


	/**
	 * @param waveStarted the waveStarted to set
	 */
	public void setWaveStarted(boolean waveStarted) {
		this.waveStarted = waveStarted;
	}

	/**
	 * @return the waveGenerator
	 */
	public WaveGenerator getWaveGenerator() {
		return waveGenerator;
	}

	/**
	 * @param waveGenerator the waveGenerator to set
	 */
	public void setWaveGenerator(WaveGenerator waveGenerator) {
		this.waveGenerator = waveGenerator;
	}

	/**
	 * Return game over, if the all the players
	 * towers are destroyed.
	 * @return
	 */
	public boolean isOver() {
		return playerTowers.size() <= 0;
	}
}