package com.qmul.tdgame.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.enums.State;

/**
 * The main view in which the user interacts with during game.
 * @author Imran Adan
 *
 */
public class GameActivity extends Activity implements SensorEventListener{

	public static GameActivity instance;
	
	private static final String TAG = GameActivity.class.getSimpleName();
	public static final String getTag() {return TAG;}
	
	public static final String START_WAVE = "Start Wave";
	public static final String PAUSE = "Pause";
	
	public static boolean sellingActive = false;
	public static boolean upgradingActive = false;
	public static boolean weaponPlacementActive = false;
	
	
	private static final GameController controller = GameController.getController();
	
	private Sensor accelerometer;
	private SensorManager sm;
	public float accX, accY;
	

	private static TextView name_veiw, cash_view, wave_round_view, info_view;
	public static ImageButton gameSpeedBtn, waveStartAndPauseBtn, sellBtn, upgradeBtn;
	private static DrawingPanel drawingPanel;
	
	private static ImageButton shakeImgBtn_view;
	private static ImageButton wallImgBtn_view;
	private static ImageButton shieldImgBtn_view;
	private static ImageButton turretImgBtn_view;
	private static ImageButton mineImgBtn_view;
	private static ImageButton basicTowerImgBtn_view;
	private static ImageButton advancedTowerImgBtn_view;
	private static ImageView infoView;
	
	private static int choiceNumber;
	
	private SoundPlayer soundPlayer;
	private SoundPool soundPool;

	private boolean sfxloaded = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_game);
		instance = this;
		
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		//Sound
		loadSFX();
		soundPlayer = SoundPlayer.getInstance();
		soundPlayer.setTune(R.raw.battle);
		soundPlayer.setCurrentTrack(R.raw.battle);
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		soundPlayer.getMediaPlayer().setVolume(volume, volume);
		soundPlayer.play();
		
		initialiseGame();
	}

	/**
	 * @return The Game instance.
	 */
	public static GameActivity getInstance(){
		return instance;
	}
	
	/**
	 * Initialise in game components.
	 */
	private void initialiseGame() {
		initialViewSetUp();
		initialModelsSetUp();
		setUpBtnListeners();
		initialViewUpdates();
	}
	
	/**
	 * Initialise models. 
	 */
	private void initialModelsSetUp() {
		String playerName = getIntent().getStringExtra("name");
		controller.initialiseGame(playerName);
	}

	/**
	 * Initial update of the view.
	 */
	private void initialViewUpdates() {
		name_veiw.setText(controller.getGame().getPlayer().getPLAYER_NAME());
		cash_view.setText(Integer.toString(controller.getGame().getPlayer().getCash()));
	}
	
	
	/**
	 * Initialise the view during initial setup.
	 */
	private void initialViewSetUp() {
		// Top
		name_veiw = (TextView) findViewById(R.id.player_name_txt);
		cash_view = (TextView) findViewById(R.id.player_scr_txt);
		wave_round_view = (TextView) findViewById(R.id.round_text);
		shakeImgBtn_view = (ImageButton) findViewById(R.id.imgBtn_shake);
		
		gameSpeedBtn = (ImageButton) findViewById(R.id.imgBtn_gameSpeed);
		upgradeBtn = (ImageButton) findViewById(R.id.btn_img_upgrade);
		sellBtn = (ImageButton) findViewById(R.id.btn_img_sell);
		waveStartAndPauseBtn = (ImageButton) findViewById(R.id.btn_img_start_wave);
		
		//Middle
		drawingPanel = (DrawingPanel) findViewById(R.id.drawing_panel);
		
		//Bottom
		wallImgBtn_view = (ImageButton) findViewById(R.id.wallImgBtn);
		shieldImgBtn_view = (ImageButton) findViewById(R.id.shieldImgBtn);
		turretImgBtn_view = (ImageButton) findViewById(R.id.turretImgBtn);
		mineImgBtn_view = (ImageButton) findViewById(R.id.mineImgBtn);
		basicTowerImgBtn_view = (ImageButton) findViewById(R.id.basicTowerImgBtn);
		advancedTowerImgBtn_view = (ImageButton) findViewById(R.id.advancedTowerImgBtn);
		
		infoView = (ImageView) findViewById(R.id.img_infoMsg);
		
		info_view = (TextView) findViewById(R.id.info_txt_view);
		info_view.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * On press of the shake button.
	 * @param v The shake button.
	 */
	public void onShake(View v){
		GameController.getController().earthQuakeStartTime = System.currentTimeMillis();
		GameController.getController().getGame().setGameState(State.EARTHQUAKE);
	}

	/**
	 * Set up listeners for all the buttons.
	 */
	public static void setUpBtnListeners() {
		upgradeBtn.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!upgradingActive) {
						infoView.setImageResource(R.drawable.msg_upgrade);	
						controller.getGame().setGameState(State.UPGRADE);
						upgradingActive = true;
						return true;
					}

					else if (upgradingActive) {
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(State.WAVE);
						upgradingActive = false;
						return true;
					}
				}
				return false;
			}
		});
		
		
		sellBtn.setOnTouchListener(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!sellingActive) {
						infoView.setImageResource(R.drawable.msg_sell);	
						controller.getGame().setGameState(State.SELL);
						sellingActive = true;
						return true;
					}

					else if (sellingActive) {
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(State.WAVE);
						sellingActive = false;
						return true;
					}
				}
				return false;
			}
		});
		
		
		
		turretImgBtn_view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_buildturret);	
						controller.getGame().setGameState(GameController.BUILD);
						controller.setDesiredItem(GameController.TURRET_CHAR);
						weaponPlacementActive = true;
						return true;
					}
					
					else if(weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(GameController.WAVE);
						controller.clearDesiredItem();
						weaponPlacementActive = false;
						return true;
					}
				}
				return false;
			}
		});
		
		mineImgBtn_view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_buildmine);	
						controller.getGame().setGameState(GameController.BUILD);
						controller.setDesiredItem(GameController.MINE_CHAR);
						weaponPlacementActive = true;
						return true;
					}
					
					else if(weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(GameController.WAVE);
						controller.clearDesiredItem();
						weaponPlacementActive = false;
						return true;
					}
				}
				return false;
			}
		});
				
		wallImgBtn_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!controller.getGame().isWaveStarted()){
						if(!weaponPlacementActive){
							infoView.setImageResource(R.drawable.msg_buildwall);	
							controller.getGame().setGameState(GameController.BUILD);
							controller.setDesiredItem(GameController.WALL_CHAR);
							weaponPlacementActive = true;
							return true;
						}
						
						else if(weaponPlacementActive){
							infoView.setImageResource(R.drawable.msg_default);	
							controller.getGame().setGameState(GameController.WAVE);
							controller.clearDesiredItem();
							weaponPlacementActive = false;
							return true;
						}
					}
				}
				return false;
			}
		});
		
		shieldImgBtn_view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_buildshield);
						controller.getGame().setGameState(GameController.BUILD);
						controller.setDesiredItem(GameController.SHIELD_CHAR);
						weaponPlacementActive = true;
						return true;
					}
					
					else if(weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(GameController.WAVE);
						controller.clearDesiredItem();
						weaponPlacementActive = false;
						return true;
					}
				}
				return false;
			}
		});

		basicTowerImgBtn_view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_build_basictower);	
						controller.getGame().setGameState(GameController.BUILD);
						controller.setDesiredItem(GameController.BASICTOW_CHAR);
						weaponPlacementActive = true;
						return true;
					}
					
					else if(weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(GameController.WAVE);
						controller.clearDesiredItem();
						weaponPlacementActive = false;
						return true;
					}
				}
				return false;
			}
		});
		
		advancedTowerImgBtn_view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(!weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_build_advantower);	
						controller.getGame().setGameState(GameController.BUILD);
						controller.setDesiredItem(GameController.ADVANCEDTOW_CHAR);
						weaponPlacementActive = true;
						return true;
					}
					
					else if(weaponPlacementActive){
						infoView.setImageResource(R.drawable.msg_default);	
						controller.getGame().setGameState(GameController.WAVE);
						controller.clearDesiredItem();
						weaponPlacementActive = false;
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * On press of the fast forward button.
	 * @param v The fast forward button.
	 */
	public void onGameSpeed(View v){
		if(!drawingPanel.mainThread.isFastFoward()){
			drawingPanel.mainThread.setFastFoward(true);
			gameSpeedBtn.setImageResource(R.drawable.btn_normal_speed);
		}else{
			drawingPanel.mainThread.setFastFoward(false);
			gameSpeedBtn.setImageResource(R.drawable.btn_fast_forward);
		}
	}


	
	/**
	 * On press of the wave start and pause buttons.
	 * @param v The start wave and pause button.
	 */
	public void onStartStop(View v){
		if(!controller.getGame().isWaveStarted()){
			infoView.setImageResource(R.drawable.msg_default);	
			waveStartAndPauseBtn.setImageResource(R.drawable.btn_pause);
			wallImgBtn_view.setImageResource(R.drawable.btn_wall_disabled);
			controller.startWavePressed();
			playSFX(SoundPlayer.SFX.SOUND_INCOMING);
		}
		
		else if(controller.getGame().isWaveStarted()){
			waveStartAndPauseBtn.setImageResource(R.drawable.btn_pause);
			infoView.setImageResource(R.drawable.msg_destroy);	
			drawingPanel.mainThread.setPaused(true);
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE: 
						drawingPanel.mainThread.setPaused(false);
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pause");
			builder.setMessage("Paused").setPositiveButton("Back", dialogClickListener).show();
		}   
	}
	
	/**
	 * Update the Info panel with the specified message.	
	 * @param message The message to be displayed.
	 */
	public static void updateInfoPanel(String message) {
		info_view.setText(message);
	}

	/**
	 * Change and update the image of the wave start button.
	 * @param label The label points to the correct resource.
	 */
	public void updateWavePauseBtn(final int label) {
		runOnUiThread(new Runnable() {
		     public void run() {
		 		waveStartAndPauseBtn.setImageResource(label);
		    };
		});
	}
	
	/**
	 * Change and update the image of the wall button.
	 * @param label The label points to the correct resource.
	 */
	public void updateWallBtn(final int label){
		runOnUiThread(new Runnable() {
		     public void run() {
		 		wallImgBtn_view.setImageResource(label);
		    };
		});
	}

	/**
	 * Update the player's cash view.
	 * @param cash The amount.
	 */
	public void updateCashView(final int cash) {
		runOnUiThread(new Runnable() {
		     public void run() {
		 		cash_view.setText(Integer.toString(cash));
		    };
		});
	}
	
	/**
	 * Update the wave round number.
	 * @param roundNumber
	 */
	public void updateWaveRoundView(final int roundNumber) {
		runOnUiThread(new Runnable() {
		     public void run() {
		 		if(roundNumber == 0){
		 			wave_round_view.setText("Start Next Wave");
		 		}else{
		 			wave_round_view.setText("Wave: " + roundNumber);
		 		}
		    };
		});
	}
	
	
	@Override
	public void onBackPressed() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE: onQuit(); break;
				case DialogInterface.BUTTON_NEGATIVE: return;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("QUIT");
		builder.setMessage("ARE YOU SURE YOU WANT TO QUIT?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}
	
	/**
	 * On back press. Prepare to quit the game.
	 */
	private void onQuit() {
		soundPlayer.destroy();	
		finish();
		Intent intent = new Intent(GameActivity.this, MainMenu.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/**
	 * Perform Tear down on the activity by shutting down any open threads.
	 */
	public void tearDown(String name, int cash) {
		Log.d(TAG, "PREPARING TEARDOWN... " + name + " " + cash);
		drawingPanel.mainThread.setRunning(false);
		this.finish();
		Intent intent = new Intent(getBaseContext(), GameOver.class);
		intent.putExtra("name", name);
		intent.putExtra("score", Integer.toString(cash));
		startActivity(intent);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(controller.getGame().isOver()){
			boolean retry = true;
			while (retry) {
				try {
					drawingPanel.mainThread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
			Log.d(TAG, "SHUTDOWN THE THREAD, PREPEARING TO OPEN NEXT ACTIVITY" );
		}
		//soundPlayer.pause();	
	}

	/**
	 * Update the information message displayed about each item the player interacts with.
	 * @param label The label the point to the correct resource.
	 */
	public void updateInfoImage(final int label){
		runOnUiThread(new Runnable() {
		     public void run() {
		 		infoView.setImageResource(label);
		    };
		});
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(!sfxloaded )
			loadSFX();
		
		if(soundPlayer.isManagerStarted() && soundPlayer.isPaused())
			soundPlayer.play();
		else
			soundPlayer.restart();
	}

	
	/**
	 * Load the required sound effects.
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
    public void loadSFX() {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                    int status) {
                sfxloaded = true;
            }
        });
        
        SoundPlayer.SFX.SOUND_INCOMING = soundPool.load(this, R.raw.incoming, 1);
        SoundPlayer.SFX.SOUND_SOLD = soundPool.load(this, R.raw.sell, 1);
        SoundPlayer.SFX.SOUND_UPGRADE = soundPool.load(this, R.raw.upgrade, 1);
        SoundPlayer.SFX.SOUND_SHOT = soundPool.load(this, R.raw.shot, 1);
        SoundPlayer.SFX.SOUND_ROCKET = soundPool.load(this, R.raw.rockets1, 1);
        SoundPlayer.SFX.SOUND_ERROR = soundPool.load(this, R.raw.error, 1);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		soundPlayer.pause();
	}
	
	/**
	 * Play a sound effect. 
	 * @param sound The sound the point to the corresponding sound effect.
	 */
	public void playSFX(int sound){
      AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
      float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
      float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      float volume = actualVolume / maxVolume;
  
      if (sfxloaded) {
          soundPool.play(sound, 0.5f, 0.5f, 1, 0, 1f);
      }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(GameController.getController().getGame().getGameState() ==  State.EARTHQUAKE){		
			GameController.getController().updateAccValues(event.values[0], event.values[1]);
		}
	}
	
	/**
	 * On the press of the earthquake button.
	 * @param on To turn on the feature or not.
	 */
	public void earthQuakeFeature(final boolean on){
		runOnUiThread(new Runnable() {
		     public void run() {
		 		if(!on) shakeImgBtn_view.setVisibility(View.INVISIBLE);
                else{
					shakeImgBtn_view.setVisibility(View.VISIBLE);			
				}	
		    };
		});
	}
}
