/**
 * 
 */
package com.qmul.tdgame.model.asset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Canvas;
import android.util.Log;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.interfaces.Renderable;
import com.qmul.tdgame.model.interfaces.Updateable;

/**
 * Wave 
 * @author Imran
 *
 */
public class Wave implements Updateable, Renderable {
	
	private static final String TAG = Wave.class.getSimpleName();
	public static String getTag() { return TAG; } 
	

	private int waveNumber;
	private final int enemyAmount;
	private int start_tower_amount;
	private float waveSpeed;
	private int killCount;
	
	private Map<Class<? extends Enemy>, Integer> successMap;
	
	private int waveHealth;
	private int waveDamage;
	private int number_of_drillers;
	private int number_of_rollers;
	
	private double successRate;
	private final List<Enemy> enemies;
	private int rollerSuccess;
	private int drillerSuccess;
	private int totalSuccess;


	/**
	 * Create a new wave with the wave number and the total amount of enemies.
	 * @param wave_number The wave number.
	 * @param amount_of_enemies The amount of enemies in this wave.
	 */
	public Wave(final int wave_number, final int amount_of_enemies) {
		waveNumber = wave_number;
		successRate = 0;
		successMap = new HashMap<Class<? extends Enemy>, Integer>();
		enemyAmount = amount_of_enemies;
		enemies = new CopyOnWriteArrayList<Enemy>();
		killCount = 0;
	}

	/**
	 * @return the waveNumber
	 */
	public int getWaveNumber() {
		return waveNumber;
	}

	/**
	 * @return the enemyAmount
	 */
	public int getEnemyAmount() {
		return enemyAmount;
	}

	/**
	 * @return the enemies
	 */
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	
	public void addEnemy(Enemy e){
		enemies.add(e);
	}

	/**
	 * @return the successRate
	 */
	public double getSuccessRate() {
		return successRate;
	}

	/**
	 * @param successRate the successRate to set
	 */
	public void setSuccessRate(float successRate) {
		this.successRate = successRate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Wave [waveNumber=" + waveNumber + ", enemyAmount="
				+ enemyAmount + ", enemies=" + enemies.size() + "]";
	}

	@Override
	public <T> void update(T... properties) {
		if (getEnemies().size() <= 0) {
			GameController.getController().getGame().releaseNextEnemy();
		} else {
			for (int i = 0; i < getEnemies().size(); i++) {
				getEnemies().get(i).update();
				if (killCount < enemyAmount) {
					GameController.getController().getGame().releaseNextEnemy();
				}
			}
		}

	}


	/**
	 * @return the successMap
	 */
	public Map<Class<? extends Enemy>, Integer> getSuccessMap() {
		return successMap;
	}

	/**
	 * @param successMap the successMap to set
	 */
	public void setSuccessMap(Map<Class<? extends Enemy>, Integer> successMap) {
		this.successMap = successMap;
	}

	/**
	 * @return the towerAmount
	 */
	public int getTowerAmount() {
		return start_tower_amount;
	}

	/**
	 * @param towerAmount the towerAmount to set
	 */
	public void setTowerAmount(int towerAmount) {
		this.start_tower_amount = towerAmount;
	}
	
	public void setWaveSpeed(float speed){
		this.waveSpeed = speed;
	}
	
	public float getWaveSpeed(){
		return this.waveSpeed;
	}

	@Override
	public void draw(Canvas canvas) {
		for(Enemy e: getEnemies()){
			e.draw(canvas);
		}
	}

	/**
	 * Inform the wave generator of certain success values.
	 */
	public void informGenerator() {		
		successMap.put(Driller.class, drillerSuccess);
		successMap.put(Roller.class, rollerSuccess);
		double diff = start_tower_amount - GameController.getController().getGame().getPlayerTowers().size();
		successRate = diff/start_tower_amount ;

		// Updating the wave generator.
		GameController.getController().getGame().getWaveGenerator().getLastGeneratedWave().successMap.put(Driller.class, drillerSuccess);
		GameController.getController().getGame().getWaveGenerator().getLastGeneratedWave().successMap.put(Roller.class, rollerSuccess);
		GameController.getController().getGame().getWaveGenerator().getLastGeneratedWave().successRate = diff/start_tower_amount;
	}
	
	/**
	 * Increase the driller success rate.
	 * @param rate
	 */
	public void updatDrillerSuccesses(int rate){
		drillerSuccess += rate;
	}
	
	/**
	 * Increase the roller success rate.
	 * @param rate
	 */
	public void updatRollerSuccesses(int rate){
		rollerSuccess += rate;
	}

	/**
	 * @return the killCount
	 */
	public final int getKillCount() {
		return killCount;
	}

	/**
	 * @param killCount the killCount to set
	 */
	public final void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	/**
	 * @return the number_of_drillers
	 */
	public final int getNumber_of_drillers() {
		return number_of_drillers;
	}

	/**
	 * @return the number_of_rollers
	 */
	public final int getNumber_of_rollers() {
		return number_of_rollers;
	}

	/**
	 * @param number_of_drillers the number_of_drillers to set
	 */
	public final void setNumber_of_drillers(int number_of_drillers) {
		this.number_of_drillers = number_of_drillers;
	}

	/**
	 * @param number_of_rollers the number_of_rollers to set
	 */
	public final void setNumber_of_rollers(int number_of_rollers) {
		this.number_of_rollers = number_of_rollers;
	}

	/**
	 * @return the waveHealth
	 */
	public final int getWaveHealth() {
		return waveHealth;
	}

	/**
	 * @return the waveDamage
	 */
	public final int getWaveDamage() {
		return waveDamage;
	}

	/**
	 * @param waveHealth the waveHealth to set
	 */
	public final void setWaveHealth(int waveHealth) {
		this.waveHealth = waveHealth;
	}

	/**
	 * @param waveDamage the waveDamage to set
	 */
	public final void setWaveDamage(int waveDamage) {
		this.waveDamage = waveDamage;
	}
}
