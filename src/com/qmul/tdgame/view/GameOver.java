package com.qmul.tdgame.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qmul.tdgame.R;

/**
 * The activity that represents the game over screen.
 * @author Imran Adan
 *
 */
public class GameOver extends Activity {
	
	private static final String TAG = GameOver.class.getSimpleName();
	public static String getTag(){ return TAG; }

	private static final String FILENAME = "scores.txt";
	private static File SAVE_FILE;
	private SoundPlayer soundPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_over);
		
		soundPlayer = SoundPlayer.getInstance();
		soundPlayer.setTune(R.raw.over);
		soundPlayer.setCurrentTrack(R.raw.over);
		soundPlayer.play();
		
		
		SAVE_FILE = new File(getFilesDir(), FILENAME);
		configureSaveFile();
		configureGameOverMessage();
	}
	
	
	/**
	 * The game over message that displays the players name and their 
	 * scores.
	 */
	private void configureGameOverMessage() {
		TextView msg = (TextView) findViewById(R.id.game_over_msg);
		String name = getIntent().getStringExtra("name");
		String score = getIntent().getStringExtra("score");
		msg.setText(name + "  " + score);
		save(name, Integer.parseInt(score));
	}



	/**
	 * On press of the main menu button.
	 * @param v
	 */
	public void onMainMenu(View v){
		this.finish();
	//	Intent intent = new Intent(getApplicationContext(), MainMenu.class);
	//	startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	/**
	 * On the press of the scores button.
	 * @param v
	 */
	public void onScores(View v){
		this.finish();
		Intent intent = new Intent(getApplicationContext(), Scores.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	/**
	 * Configure the save file, where a new file is created 
	 * on a new device this application is installed in.
	 */
	private void configureSaveFile() {
		if (!SAVE_FILE.exists())
			try {
				SAVE_FILE.createNewFile();
			} catch (IOException e) {
				this.finish();
				e.printStackTrace();
			}	
	}
	

	/**
	 * Save the player's high scores.
	 * @param name The name of the player.
	 * @param score The score of the player.
	 */
	public void save(String name, int score){
		try{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(SAVE_FILE, true));
			bufferedWriter.write(formmatedSaveString(name, score));
			Log.d(TAG, "Saved... " + formmatedSaveString(name, score));
			bufferedWriter.close();
		}catch(IOException e){
			Log.e(TAG, e.toString());
			e.printStackTrace();
			this.finish();
		}
	}
	
	/**
	 * The formated String to save.
	 * @param name The name of the player.  
	 * @param score The score.
	 * @return a formatted string.
	 */
	public String formmatedSaveString(String name, int score){
		return name + "=" + score+"\n";
	}
	

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	public void onBackPressed(){
		this.finish();
		Intent intent = new Intent(getApplicationContext(), MainMenu.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(soundPlayer.isManagerStarted() && soundPlayer.isPaused())
			soundPlayer.play();
		else
			soundPlayer.restart();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		soundPlayer.pause();
	}
}
