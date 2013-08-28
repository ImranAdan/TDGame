package com.qmul.tdgame.view;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ViewFlipper;

/**
 * Activity that represents the Help screen.
 * @author Imran
 *
 */
public class Tutorial extends Activity {
	
	private ViewFlipper flipper;
	private SoundPlayer soundPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_tutorial);
	
		GameResources.currentContext = this;
		soundPlayer = SoundPlayer.getInstance();
		
		initComp();
	}
	
	/**
	 * Initialise the required class components.
	 */
	private void initComp() {
		flipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		flipper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flipper.showNext();
			}
		});
	}

	public void onBackPressed(){
		this.finish();
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
