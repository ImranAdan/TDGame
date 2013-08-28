package com.qmul.tdgame.view;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * The activity that represents the player details screen.
 * @author Imran
 *
 */
public class PlayerDetails extends Activity {

	private SoundPlayer soundPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_player_details);
		
		GameResources.currentContext = this;
		soundPlayer = SoundPlayer.getInstance();
	}
	
	/**
	 * Start a new Game Activity. 
	 * @param v
	 */
	public void onStart(View v){
		String playerName = ((EditText) findViewById(R.id.playerName)).getText().toString();
		Intent intent = new Intent(getApplicationContext(), WalkThrough.class);
		intent.putExtra("name", playerName);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
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
		soundPlayer.pause();
		super.onBackPressed();
	}
}
