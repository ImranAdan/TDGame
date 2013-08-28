package com.qmul.tdgame.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.qmul.tdgame.view.GameActivity;
import com.qmul.tdgame.R;
import com.qmul.tdgame.view.SoundPlayer;
import com.qmul.tdgame.model.asset.AdvancedTower;
import com.qmul.tdgame.model.asset.BasicTower;
import com.qmul.tdgame.model.asset.Enemy;
import com.qmul.tdgame.model.asset.Game;
import com.qmul.tdgame.model.asset.GameMap;
import com.qmul.tdgame.model.asset.Item;
import com.qmul.tdgame.model.asset.Mine;
import com.qmul.tdgame.model.asset.Shield;
import com.qmul.tdgame.model.asset.Tower;
import com.qmul.tdgame.model.asset.Turret;
import com.qmul.tdgame.model.asset.Wall;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.model.enums.State;
import com.qmul.tdgame.util.DeviceResources;

/**
 * Main Game Controller that gets actions from the view and update the models
 * which in turn the views accordingly.
 * 
 * @author Imran
 * 
 */
public final class GameController {

	public static final String TAG = GameController.class.getSimpleName();
	
	private static final long QUAKE_LIMIT = 3000;
	public float accX;
	public float accY;

	public static final State BUILD = State.BUILD;
	public static final State WAVE = State.WAVE;
	
	public static final char TURRET_CHAR = 't';
	public static final char MINE_CHAR = 'm';
	public static final char WALL_CHAR = 'w';
	public static final char SHIELD_CHAR = 's';
	public static final char BASICTOW_CHAR = 'b';
	public static final char ADVANCEDTOW_CHAR = 'a';

	private static GameController controller;
	
	public long earthQuakeStartTime;
	private Game game;
	private char desiredItem;

	/**
	 * Create an instance of the Game Controller.
	 */
	private GameController() {
		game = null;
		setDesiredItem('e');
	}

	/**
	 * Get an instance of the game controller.
	 * @return
	 */
	public static GameController getController() {
		if (controller == null)
			controller = new GameController();
		return controller;
	}

	/**
	 * Create a new game with a player.
	 * @param playerName
	 */
	public void initialiseGame(String playerName) {
		this.game = new Game(playerName);
	}

	/**
	 * Setup the initial drawing panel with 
	 * the width and height.
	 * @param width
	 * @param height
	 */
	public void initialDrawingPanelSetUp(int width, int height) {
		game.setBackground(Bitmap.createScaledBitmap(BitmapFactory .decodeResource(DeviceResources.getResources(), R.drawable.level_bg_r_one), width, height, true));
		game.setMap(new GameMap(width, height));
		game.setPlayerTowers(Tower.spwanInitialSet());
	}

	/**
	 * Update all the models in the game.
	 */
	public void updateModels() {
		if (game != null) {
			if(game.isOver()){
				performGameTearDown();
				return;
			}else{		
				game.getMap().update();
				updateTowers();
				updatePlayerItems();
				if (game.isWaveStarted()) {
					updateWave();
				}
			}
		}
	}


	/**
	 * Render in game objects to the canavs.
	 * @param canvas
	 */
	public void renderGame(Canvas canvas) {
		if (game != null) {
			canvas.drawBitmap(game.getBackground(), 0, 0, null);
			for (Tower T : game.getPlayerTowers())
				T.draw(canvas);
			for (Item I : game.getPlayerItems())
				I.draw(canvas);
			
			if (game.isWaveStarted()) {
				game.getCurrentWave().draw(canvas);
			}	
			game.getMap().draw(canvas);
		}
	}

	
	/**
	 * Initiate the game tear down process.
	 */
	private void performGameTearDown() {
		GameActivity.getInstance().tearDown(game.getPlayer().getPLAYER_NAME(), game.getPlayer().getCash());
	}
	

	/**
	 * Update all the items owend by the player.
	 */
	private void updatePlayerItems() {
		for (int i = 0; i< game.getPlayerItems().size(); i++){
			if(game.getPlayerItems().get(i).isDestroyed()){
				game.getPlayerItems().get(i).free();
				game.getPlayerItems().remove(i);
				i--;
			}else{
				game.getPlayerItems().get(i).update();
			}
		}
	}

	/**
	 * Update all the towers owned by the player.
	 */
	private void updateTowers() {
		for (int i = 0; i< game.getPlayerTowers().size(); i++){
			if(game.getPlayerTowers().get(i).isDead()){
				game.getPlayerTowers().get(i).free();
				game.getPlayerTowers().remove(i);
				i--;
			}else{
				game.getPlayerTowers().get(i).update();
			}
		}
	}

	/**
	 * Update the wave.
	 */
	private void updateWave() {
		
		/*
		 * IMPORTANT !!!!! - uncomment this to turn earthquake feature across every 5 waves. 
		 */
		
//		if(game.getCurrentWave().getWaveNumber() % 5 == 0)
//			GameActivity.getInstance().earthQuakeFeature(true);
//		else
//			GameActivity.getInstance().earthQuakeFeature(false);
		
		game.getCurrentWave().update();
		for(Enemy e: game.getCurrentWave().getEnemies()){
			if(e.isDead()){
				game.getCurrentWave().setKillCount(game.getCurrentWave().getKillCount() + 1);
				game.getPlayer().setCash(game.getPlayer().getCash() + e.getDeathPrice());
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getCurrentWave().getEnemies().remove(e);
			}
		}

		
		if(game.getCurrentWave().getKillCount() >= game.getCurrentWave().getEnemyAmount()){
			GameActivity.getInstance().updateWaveRoundView(0);
			GameActivity.getInstance().updateWavePauseBtn(R.drawable.btn_start_wave);
			GameActivity.getInstance().updateWallBtn(R.drawable.btn_wall);
			game.getCurrentWave().informGenerator();
			game.setWaveStarted(false);
		}
	}


	/**
	 * Spawn an item at locations x,y.
	 * @param x
	 * @param y
	 */
	public void spawn(float x, float y){
		Tile dst = game.getMap().getGame2DGrid().getTileAt(x, y);
		if(dst.isOccupied() && desiredItem != SHIELD_CHAR)
			GameActivity.getInstance().updateInfoImage(R.drawable.msg_builderror);
		else if(dst.isOccupied() && desiredItem == SHIELD_CHAR){
			for(Item i: game.getPlayerItems()){
				if(i.getTILE().x == dst.x && i.getTILE().y == dst.y){
					GameActivity.getInstance().updateInfoImage(R.drawable.msg_builderror);
					return;
				}
			}
			performPlacement(dst);
		}
		else
			performPlacement(dst);
	}
	
	
	/**
	 * Perform the placement of an object in a tile.
	 * @param dst
	 */
	private void performPlacement(Tile dst) {
		switch (desiredItem) {
		case TURRET_CHAR:
			if(game.getPlayer().getCash() >= Turret.INITIAL_STARING_PRICE){
				game.getPlayer().setCash(game.getPlayer().getCash() - Turret.INITIAL_STARING_PRICE);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getPlayerItems().add(new Turret(dst));
			}else{
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
				return;
			}
			break;
		case MINE_CHAR:
			if(game.getPlayer().getCash() >= Mine.INITIAL_STARING_PRICE){
				game.getPlayer().setCash(game.getPlayer().getCash() - Mine.INITIAL_STARING_PRICE);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getPlayerItems().add(new Mine(dst));
			}else{
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
				return;
			}
			break;
		case WALL_CHAR:
			if(game.getPlayer().getCash() >= Wall.INITIAL_STARING_PRICE){
				game.getPlayer().setCash(game.getPlayer().getCash() - Wall.INITIAL_STARING_PRICE);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getPlayerItems().add(new Wall(dst));
			}else{
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
				return;
			}
			break;
		case SHIELD_CHAR:
			if (dst.getOccupiedBy() != 'T') { 
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_shielderror);
					return;
				} 
			else{ 
				if(game.getPlayer().getCash() >= Shield.INITIAL_STARING_PRICE){
					game.getPlayer().setCash(game.getPlayer().getCash() - Shield.INITIAL_STARING_PRICE);
					GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
					game.getPlayerItems().add(new Shield(dst));
				}else{
					GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
					return;
				}
			}
			break;
		case BASICTOW_CHAR:
			if(game.getPlayer().getCash() >= BasicTower.INITIAL_STARING_PRICE){
				game.getPlayer().setCash(game.getPlayer().getCash() - BasicTower.INITIAL_STARING_PRICE);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getPlayerTowers().add(new BasicTower(dst)); 
			}else{
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
				return;
			}
			break;
		case ADVANCEDTOW_CHAR:
			if(game.getPlayer().getCash() >= AdvancedTower.INITIAL_STARING_PRICE){
				game.getPlayer().setCash(game.getPlayer().getCash() - AdvancedTower.INITIAL_STARING_PRICE);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				game.getPlayerTowers().add(new AdvancedTower(dst)); 
			}else{
				GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
				return;
			}
			break;
		}
	}


	/**
	 * Get the game instance.
	 * @return
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Clear the desire item.
	 */
	public void clearDesiredItem() {
		desiredItem = 'e';
	}

	/**
	 * get the desired item.
	 * @return
	 */
	public char getDesiredItem() {
		return desiredItem;
	}

	/**
	 * set the desired item that the player wishes to place.
	 * @param desiredItem
	 */
	public void setDesiredItem(char desiredItem) {
		this.desiredItem = desiredItem;
	}
	


	/**
	 * On the press of the start wave button.
	 */
	public void startWavePressed() {
		game.setModelWave(game.getWaveGenerator().generateNextWave());
		game.setUpCurrentWave();
		GameActivity.getInstance().updateWaveRoundView(game.getCurrentWave().getWaveNumber());
		game.setWaveStarted(true);
	}
	

	/**
	 * Sell at locations x,y
	 * @param x
	 * @param y
	 */
	public void sellAt(float x, float y) {
		Tile selectedTile = game.getMap().getGame2DGrid().getTileAt(x, y);
		boolean sold = false;
		sold =  sellItem(selectedTile);
		if(!sold)
			sellTower(selectedTile);
	}

	/**
	 * Sell tower at locations x,y
	 */
	private boolean sellTower(Tile selectedTile) {
		if(game.getPlayerTowers().size() <= 1 && game.getPlayerTowers().get(0).getTILE().equals(selectedTile)){
			GameActivity.getInstance().updateInfoImage(R.drawable.msg_lastsell);
			GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_ERROR);
			return false;
		}
		
		for (Tower t : game.getPlayerTowers()) {
			if (t.getTILE().equals(selectedTile)) {
				game.getPlayer().setCash(game.getPlayer().getCash() + t.getPrice() / 2);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				t.free();
				game.getPlayerTowers().remove(t);
				GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_SOLD);
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Sell item at locations x,y
     *
	 */
	private boolean sellItem(Tile selectedTile) {
		for (Item item : game.getPlayerItems()) {
			if (item.getTILE().equals(selectedTile)) {
				game.getPlayer().setCash(game.getPlayer().getCash() + item.getPrice() / 2);
				GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
				item.free();
				item.setDestroyed(true);
				GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_SOLD);
				return true;
			}
		}
		return false;
	}


	/**
	 * Upgrade at locations x,y 
	 * @param x
	 * @param y
	 */
	public void upgradeAt(float x, float y) {
		Tile selectedTile = game.getMap().getGame2DGrid().getTileAt(x, y);
		boolean upgraded = false;
		upgraded = upgradeItem(selectedTile);
		if(!upgraded)
			upgradeTower(selectedTile);
	}
	
	/**
	 * Upgrade tower at locations x,y
	 */
	private void upgradeTower(Tile selectedTile) {
		for(Tower tower: game.getPlayerTowers()){
			if(tower.getTILE().equals(selectedTile)){
				if(tower instanceof AdvancedTower){
					if(!tower.upgraded()){
						if(game.getPlayer().getCash() >= AdvancedTower.UPGRADE_PRICE){
							
							Log.d("ITEM", "Upgrade Price= " + AdvancedTower.UPGRADE_PRICE + " " + tower.getClass().getSimpleName());
							
							game.getPlayer().setCash(game.getPlayer().getCash() - AdvancedTower.UPGRADE_PRICE);
							GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
							tower.upgrade();
							GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_UPGRADE);
							
						}
						else{
							GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
							GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_ERROR);
						}
					}else{
						GameActivity.getInstance().updateInfoImage(R.drawable.msg_maxlevel);
						GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_ERROR);
					}
					return;
				}else{
					GameActivity.getInstance().updateInfoImage(R.drawable.msg_cannotupgradebasic);
					GameActivity.getInstance().playSFX(	SoundPlayer.SFX.SOUND_ERROR);
					return;
				}
			}
		}
	}

	/**
	 * Upgrade item at locations x,y
	 */
	private boolean upgradeItem(Tile selectedTile) {
		for(Item item: game.getPlayerItems()){
			if(item.getTILE().equals(selectedTile)){
				if(!item.isUpgraded()){
					if(game.getPlayer().getCash() > item.getUpgradePrice()){
						Log.d("ITEM", "Upgrade Price= " + item.getUpgradePrice() + ", " + item.getClass().getSimpleName());	
						game.getPlayer().setCash(game.getPlayer().getCash() - item.getUpgradePrice());
						GameActivity.getInstance().updateCashView(game.getPlayer().getCash());
						item.upgrade();
						GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_UPGRADE);
					}
					else{
						GameActivity.getInstance().updateInfoImage(R.drawable.msg_funds);
						GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_ERROR);
					}
				}else{
					GameActivity.getInstance().updateInfoImage(R.drawable.msg_maxlevel);
					GameActivity.getInstance().playSFX(SoundPlayer.SFX.SOUND_ERROR);
				}
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Play a sound effect.
	 * @param sfx The sound effect to be played.
	 */
	public void playSFX(int sfx){
		GameActivity.getInstance().playSFX(sfx);
	}
	

	/**
	 * Update the accelerometer values.
	 * @param x
	 * @param y
	 */
	public void updateAccValues(float x, float y) {
		accX = x;
		accY = y;
		if(earthQuakeFinished()){
			game.setGameState(WAVE);
			accX = 0f;
			accY = 0f;
		}
	}

	/**
	 * Calculate if the earthquake period has finished.
	 * @return
	 */
	public boolean earthQuakeFinished() {
		if(System.currentTimeMillis() >= earthQuakeStartTime + QUAKE_LIMIT)
			return true;
		return false;
	}







}
