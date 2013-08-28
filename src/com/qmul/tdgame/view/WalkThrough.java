package com.qmul.tdgame.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ViewFlipper;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

/**
 * Activity that represents the walkthrough screen.
 * @author Imran
 *
 */
public class WalkThrough extends Activity {
	
	private static final String TAG = WalkThrough.class.getSimpleName();
	
	private ViewFlipper flipper;
	private SoundPlayer soundPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_walk_through);
	
		GameResources.currentContext = this;
		soundPlayer = SoundPlayer.getInstance();
		initComp();
	}
	

	
	/**
	 * Initialise the required class components.
	 */
	private void initComp() {
		flipper = (ViewFlipper) findViewById(R.id.walk_through_flipper);
		flipper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flipper.showNext();
			}
		});
	}
	
	/**
	 * On the press of the skip button.
	 * @param v
	 */
	public void onSkip(View v){
		String playerName = getIntent().getStringExtra("name");
		Log.d(TAG, "The Player name is: " + playerName);
		Intent intent = new Intent(getApplicationContext(), GameActivity.class);
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
