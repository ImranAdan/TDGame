package com.qmul.tdgame.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

/**
 * The activity that represents the main menu screen.
 * @author Imran Adan
 *
 */
public class MainMenu extends Activity {
	
	private static final String TAG = MainMenu.class.getSimpleName();
	private int buttonPressedSound;	
	private boolean sfxloaded = false;

	private SoundPlayer soundPlayer;
	private SoundPool soundPool;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_main_menu);
		
		GameResources.currentContext = this;                                                                                                                                                    
		loadSFX();
		soundPlayer = SoundPlayer.getInstance();
		soundPlayer.setTune(R.raw.prep);
		soundPlayer.setCurrentTrack(R.raw.prep);
		soundPlayer.play();
	}

	/**
	 * On the press of the new game button.
	 * @param v
	 */
	public void onNewGame(View v){
		playSFX(buttonPressedSound);
		Intent intent = new Intent(getBaseContext(), PlayerDetails.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/**
	 * On the press of the help button.
	 * @param v
	 */
	public void onTutorial(View v){
		playSFX(buttonPressedSound);
		Intent intent = new Intent(getBaseContext(), Tutorial.class);
		this.startActivity(intent);
	}
	
	
	/**
	 * On the press of the scores button.
	 * @param v
	 */
	public void onScores(View v){
		playSFX(buttonPressedSound);
		Intent intent = new Intent(getBaseContext(), Scores.class);
		this.startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	

	/**
	 * On the press of the quit button.
	 * @param v
	 */
	public void onQuit(View v) {
		playSFX(buttonPressedSound);
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE: MainMenu.this.finish(); 
				case DialogInterface.BUTTON_NEGATIVE: return;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("QUIT");
		builder.setMessage("ARE YOU SURE YOU WANT TO QUIT?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}
	

	
	/**
	 * On the press of the scores button.
	 * @param v
	 */
	public void scores(View v){
		Intent intent = new Intent(getApplicationContext(), Scores.class);
		startActivity(intent);
	}
	
	/*
	 * test function - ignore
	 */
	public void onTest(View v){
		playSFX(buttonPressedSound);
	}
	

	/**
	 * Load all sound effects.
	 */
	private void loadSFX() {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                    int status) {
                sfxloaded = true;
            }
        });
        
        buttonPressedSound = soundPool.load(this, SoundPlayer.BUTTON_PRESSED, 1);
	}
	
	/**
	 * Play a sound effect.
	 * @param sound The sound effect.
	 */
	private void playSFX(int sound){
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        if (sfxloaded) {
            soundPool.play(sound, 5, 5, 1, 0, 1f);
        }
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		if(soundPlayer.getCurrentTrack() != R.raw.prep)
			soundPlayer.setTune(R.raw.prep);
		soundPlayer.play();		
	}

	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		soundPlayer.pause();
		super.onPause();

	}

	public void onBackPressed(){
		this.finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}
