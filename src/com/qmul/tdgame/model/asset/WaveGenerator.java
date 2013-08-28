/**
 * 
 */
package com.qmul.tdgame.model.asset;


import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.PathFinder;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.core.Vector2D;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.util.RandomGenerator;
import com.qmul.tdgame.util.UtilOps;


/**
 * The wave generator is the main class used to generate waves of enemies based on user performance.
 * @author Imran
 * 
 */
public class WaveGenerator{

	public static final String TAG = WaveGenerator.class.getSimpleName();
	
	private static final int MAX_ENEMY_AMOUNT = 100;
	private static final int MAX_PATH_FOLLOWERS = 20;
	private static final int MAX_HEALTH = 3000;
	private static final int MAX_DAMAGE = 800;

	private static final int INIT_AMOUNT = 20;
	
	private Tile start_pos1;
	private Tile start_pos2;
	private int current_num_towers;
	private int current_num_items;
	private float current_pf_speed;
	
	private final List<Wave> generatedWaves;
	
	private final PathFinder p;
	private LinkedList<Tile> current_path;
	private final ArrayList<Float> x_path;
	private final ArrayList<Float> y_path;
	
	private GameMap map;

	/**
	 * Create a new wave generator instance.
	 */
	public WaveGenerator(){
		p = new PathFinder();
		generatedWaves = new ArrayList<Wave>();
		current_path = new LinkedList<Tile>();
		current_pf_speed = Speed.STARTING_SPEED;
		x_path = new ArrayList<Float>();
		y_path = new ArrayList<Float>();
	}

	
	/**
	 * Generate the next wave of enemies.
	 */
	public Wave generateNextWave(){
		perceiveEnvironment();
		start_pos1 = RandomGenerator.pickRandomETile();
		start_pos2 = RandomGenerator.pickRandomETile();
	
		Wave w = generateDecision();
		w.setTowerAmount(current_num_towers);
		configureTypes(w);
		configureHealth(w);
		configureDamage(w);

		decisionTearDown();
		generatedWaves.add(w);	
		
		return w;
	}


	/**
	 * Configure the Health attribute of this wave.
	 * @param w
	 */
	private void configureHealth(Wave w) {
		if(getLastGeneratedWave() == null){
			Log.d(TAG, "Initial Wave.. setting wave health to = " + Enemy.INIT_HEALTH);
			w.setWaveHealth(Enemy.INIT_HEALTH);
			
			for(Enemy e: w.getEnemies()){
				e.setStartingHealth(w.getWaveHealth());
				e.setHealth(w.getWaveHealth());
			}			
		}
		else{
			Log.d(TAG, "Adding 40, Previous wave has health of = " + getLastGeneratedWave().getWaveHealth());
			int health = getLastGeneratedWave().getWaveHealth() + 40;
			Log.d(TAG, "Setting current wave health to =" + health);	
			
			if(health >= MAX_HEALTH)
				health = MAX_HEALTH;
			w.setWaveHealth(health);
			
			for(Enemy e: w.getEnemies()){
				e.setStartingHealth(w.getWaveHealth());
				e.setHealth(w.getWaveHealth());
			}
		}
	}


	/**
	 * Configure the damage attribute of this wave.
	 * @param w
	 */
	private void configureDamage(Wave w) {
		if(getLastGeneratedWave() == null){
			Log.d(TAG, "Initial Wave.. setting wave damage to = " + Enemy.INIT_DAMAGE);
			w.setWaveDamage(Enemy.INIT_DAMAGE);
		}
		else{
			Log.d(TAG, "Adding 32, Previous wave has health of = " + getLastGeneratedWave().getWaveDamage());
			int damage = getLastGeneratedWave().getWaveDamage() + 32;
			
			if(damage > MAX_DAMAGE)
				damage = MAX_DAMAGE;
			w.setWaveDamage(damage);
			
			Log.d(TAG, "Setting current wave damage to =" + damage);	

			
			for(Enemy e: w.getEnemies()){
				e.setDamage(damage);
			}
		}
	}


	/**
	 * Set the number of enemy types in the wave.
	 * @param w
	 */
	private void configureTypes(Wave w) {
		int d = 0;
		int r = 0;
		for(Enemy e : w.getEnemies()){
			if(e instanceof Driller)
				d++;
			else
				r++;
		}
		
		w.setNumber_of_drillers(d);
		w.setNumber_of_rollers(r);
	}


	/**
	 * Examine and analyse the current state of the environment.
	 * Which is the game-map. The game map object is fully observable 
	 * by any agent implementing this interface. Through this the required
	 * fields can be changed to make an appropriate action.
	 * @see GameMap
	 */
	private void perceiveEnvironment() {
		map = GameController.getController().getGame().getMap();
		current_num_towers = map.getNumberOf('T');
		current_num_items = map.getNumberOf('I');
	}

	/**
	 * Generate an appropriate action through sensing the environment.
	 * @param num_of_items 
	 * @param num_of_towers 
	 */
	private Wave generateDecision() {		
		if(generatedWaves.size() <= 0)
			return initialWave();

		return generateWave(getLastGeneratedWave());
	}
	
	
	/**
	 * Produce the initial wave.
	 * @return
	 */
	private Wave initialWave() {
		if(current_num_items >= current_num_towers){
			int seekers = Math.round(INIT_AMOUNT * 0.25f);
			int followers = Math.round(INIT_AMOUNT * 0.75f);
			return build(1, INIT_AMOUNT, seekers, followers, Speed.STARTING_SPEED);
		}
		
		int seekers = Math.round(INIT_AMOUNT * 0.5f);
		int followers = Math.round(INIT_AMOUNT * 0.5f);
		return build(1, INIT_AMOUNT, seekers, followers, Speed.STARTING_SPEED);
	}
	
	
	/**
	 * Generate a wave base on last generated wave.
	 * @param lastGeneratedWave
	 * @return
	 */
	private Wave generateWave(Wave lastGeneratedWave) {
		int wave_number = getLastGeneratedWave().getWaveNumber() +1;
		int amount = 2 + getLastGeneratedWave().getEnemyAmount();
		if(amount > MAX_ENEMY_AMOUNT) 
			amount = MAX_ENEMY_AMOUNT;
		
		float seeking_speed = (float) ((getLastGeneratedWave().getWaveSpeed() * 0.25f) + getLastGeneratedWave().getWaveSpeed());
		if(seeking_speed > Speed.TOP_SPEED) 
			seeking_speed = Speed.TOP_SPEED;
		
		if(lastGeneratedWave.getSuccessRate() < 0.5){
			Log.d(TAG, "un-succesfull wave!! performing IF block");
			
			Log.d(TAG, "new amount = " + amount);
			
			if(current_num_items >= current_num_towers){	
				int seekers = Math.round(amount * 0.25f);
				int followers = Math.round(amount * 0.75f);
				return build(wave_number, amount, seekers, followers, seeking_speed);
			}
			else{ 
				int seekers = Math.round(amount * 0.5f);
				int followers = Math.round(amount * 0.5f);
				return build(wave_number, amount, seekers, followers, seeking_speed);
			}
		}else{
			if(lastGeneratedWave.getSuccessMap().get(Driller.class) > lastGeneratedWave.getSuccessMap().get(Roller.class)){
				Log.d(TAG, "Driller had higher success rate");
				int pfAmount = lastGeneratedWave.getNumber_of_drillers();
				pfAmount = (int) ((pfAmount * 0.5) + pfAmount);
				int seekingAmount  = amount - pfAmount;

				
				return build(wave_number, amount, seekingAmount, pfAmount, seeking_speed);
				
			}
			else{
				Log.d(TAG, "Roller had higher success rate");
				int seekingAmount = lastGeneratedWave.getNumber_of_rollers();				
				seekingAmount  = (int) ((seekingAmount  * 0.5) + seekingAmount);
				int pfAmount = amount - seekingAmount ;	
				Log.d(TAG, "pfAmount = " + pfAmount);
				Log.d(TAG, "seeking = " + seekingAmount);
				Log.d(TAG, "Amount=" + amount);
				Log.d(TAG, "total= " + (pfAmount + seekingAmount));
				return build(wave_number, amount, seekingAmount, pfAmount, seeking_speed);
			}
		}
	}

	/**
	 * Build the wave with the below attributes.
	 * @param wave_number
	 * @param amount
	 * @param seekers
	 * @param followers
	 * @param seeking_speed
	 * @return
	 */
	private Wave build(int wave_number, int amount, int seekers, int followers, float seeking_speed) {
		int total = seekers + followers;
		Wave wave = new Wave(wave_number, total);
		wave.setWaveSpeed(seeking_speed);

		Tower pathTarget = getFurthestTowerLocation(start_pos1);
		Tile furthestTowerTile = pathTarget.getTILE();
		current_path = p.getShortestPath(start_pos1, furthestTowerTile);
		
		if(current_path == null){
			createAllSeekers(wave, total, seeking_speed);
		}else{
			int pf_amount = 0;
			current_path.addFirst(start_pos1);
			current_path.addLast(furthestTowerTile);
			determinePFSpeed();
			convertPathToValues(current_pf_speed);
			int pf_counter = 0;
			for (int i = 0; i < total; i++) {
				Enemy e = null;
				if((i%2 == 0) && pf_counter < MAX_ENEMY_AMOUNT && pf_counter < followers){
					e = new Driller(start_pos2);
					e.setState(State.PATH_FOLLOWING);
					e.setSpeed(seeking_speed);
					e.setX_path((ArrayList<Float>) x_path.clone());
					e.setY_path((ArrayList<Float>) y_path.clone());
					e.setClosest(pathTarget);
					e.setNearestAvailableTowerFound(true);
					pf_counter++;
				}else{
					e = new Roller(start_pos1);
					e.setSpeed(seeking_speed);
					e.setState(State.SEEKING);
				}	
				wave.addEnemy(e);
			}
		}
		
		Collections.shuffle(wave.getEnemies());
		return wave;
	}

	
	
	// Bottom not used;
	
	

	private Wave buildWave(int wave_number, int amount, float seekingRatio, float pfRatio, float seeking_speed){

		//Set Up Wave
		Wave wave = new Wave(wave_number, amount);
		wave.setWaveSpeed(seeking_speed);
		
		//Find Shortest Path.
		Tower pathTarget = getFurthestTowerLocation(start_pos1);
		Tile furthestTowerTile = pathTarget.getTILE();
		current_path = p.getShortestPath(start_pos1, furthestTowerTile);
		
		if(current_path == null){
			pfRatio = 0;
			createAllSeekers(wave, amount, seeking_speed);
		}else{
		    int seekingAmount = Math.round((amount * seekingRatio));
		    int pf_amount = amount - seekingAmount;
			current_path.addFirst(start_pos1);
			current_path.addLast(furthestTowerTile);
			determinePFSpeed();
			convertPathToValues(current_pf_speed);
			int pf_counter = 0;
			for (int i = 0; i < amount; i++) {
				Enemy e = null;
				if(i < pf_amount && pf_counter < MAX_ENEMY_AMOUNT){
					e = new Driller(start_pos2);
					e.setState(State.PATH_FOLLOWING);
					e.setSpeed(seeking_speed);
					e.setX_path((ArrayList<Float>) x_path.clone()); // hmm caused out of memory exception .. need to double check
					e.setY_path((ArrayList<Float>) y_path.clone());
					e.setClosest(pathTarget);
					e.setNearestAvailableTowerFound(true);
					pf_counter++;
				}else{
					e = new Roller(start_pos1);
					e.setSpeed(seeking_speed);
					e.setState(State.SEEKING);
				}
				
				wave.addEnemy(e);
			}
		}
		
		//Collections.shuffle(wave.getEnemies());
		return wave;
	}
	

	

	
	

	private void createAllSeekers(Wave wave, int amount, float speed) {
		for (int i = 0; i < amount; i++) {
			Enemy e = new Roller(start_pos1);
			e.setSpeed(speed);
			e.setState(State.SEEKING);
			wave.addEnemy(e);
		}
	}


	private void determinePFSpeed() {
		current_pf_speed = (getLastGeneratedWave() == null) ? Speed.STARTING_SPEED : getLastGeneratedWave().getWaveSpeed() + 0.05f;
		if(current_pf_speed > Speed.TOP_SPEED){
			current_pf_speed = Speed.TOP_SPEED;
		}
	}
	
	private int numberOf(Wave w, char c){
		int counter = 0;
		for(Enemy e: w.getEnemies()){
			if((e instanceof Driller) && c=='d'){
				counter++;
			}
			if((e instanceof Roller) && c=='r'){
				counter++;
			}
		}
		return counter;
	}
	
	
	
	
	/**
	 * Seek the closest target.
	 */
	private Tower getFurthestTowerLocation(Tile src) {
		Tower furthestTower = GameController.getController().getGame().getPlayerTowers().get(0);
		float currentfurthest = Vector2D.length(Vector2D.subtract(new Vector2D(furthestTower.getTILE().getTileRect().centerX(), furthestTower.getTILE().getTileRect().centerY()), new Vector2D(src.getTileRect().centerX(), src.getTileRect().centerY())));
		for(int i = 1; i < GameController.getController().getGame().getPlayerTowers().size(); i++){
			Tower nextTower = GameController.getController().getGame().getPlayerTowers().get(i);
			float currentDistance = Vector2D.length(Vector2D.subtract(new Vector2D(nextTower.getTILE().getTileRect().centerX(), nextTower.getTILE().getTileRect().centerY()), new Vector2D(furthestTower.getTILE().getTileRect().centerX(), furthestTower.getTILE().getTileRect().centerY())));			
			if(currentDistance < currentfurthest){
				furthestTower = nextTower;
				currentfurthest = currentDistance;
			}
		}
		
		return furthestTower;
	}
	
	public Wave getLastGeneratedWave(){
		if(generatedWaves.size() > 0)
			return generatedWaves.get(generatedWaves.size()-1);
		return null;
	}
	
	
	private void decisionTearDown() {
		if(current_path!=null){
			current_path.clear();
			x_path.clear();
			y_path.clear();
		}	
		start_pos1 = null;
		start_pos2 = null;
	}
	
	/**
	 * Convert a set of tiles into x and y values.
	 * @param speed
	 */
	private void convertPathToValues(float speed) {
		for(int i = 0; i < current_path.size()-1; i++){
			Tile current = current_path.get(i);
			Tile next = current_path.get(i+1);
			if (next.x > current.x) {
				for (float j = current.x; j < current.endx; j += speed) {
					x_path.add(j);
					y_path.add((float) current.y);
				}
			}
			if (next.y > current.y) {
				for (float j = current.y; j < current.endy; j += speed) {
					x_path.add((float) current.x);
					y_path.add(j);
				}
			}
			if (next.x < current.x) {
				for (float j = current.x; j >= next.x; j -= speed) {
					x_path.add(j);
					y_path.add((float) current.y);
				}
			}
			if (next.y < current.y) {
				for (float j = current.y; j >= next.y; j -= speed) {
					x_path.add((float) current.x);
					y_path.add(j);
				}
			}
		}
	}
		

	/**
	 * Class that defines static constants.
	 * @author Imran
	 *
	 */
	private static final class Speed{
		public static final float STARTING_SPEED = 0.5f;
		public static final float TOP_SPEED = 5.0f;
	}
}
