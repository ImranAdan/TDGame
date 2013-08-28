package com.qmul.tdgame.view;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.DeviceResources;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Activity the represents the Splash screen.
 * @author Imran
 *
 */
public class SplashScreen extends Activity {
	
	private static final String TAG = SplashScreen.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		setDeviceResources();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				Intent intent = new Intent(SplashScreen.this, MainMenu.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			}

		}, 3000);

	}

	/**
	 * Assigin all device resources values.
	 */
	private void setDeviceResources() {
		DeviceResources.setResources(getResources());
		int w, h;
		if (Build.VERSION.SDK_INT > 12) {
			// The below Method is deprecated with OS.
			w = getWindowManager().getDefaultDisplay().getWidth();
			h = getWindowManager().getDefaultDisplay().getHeight();
		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			w = metrics.widthPixels;
			h = metrics.heightPixels;
		}
		DeviceResources.setScreenWidth(w);
		DeviceResources.setScreenHeight(h);
		
		Log.d(TAG, "Device Width: " + DeviceResources.getScreenWidth());
		Log.d(TAG, "Device Height: " + DeviceResources.getScreenHeight());
		
		if(DeviceResources.getScreenWidth() > DeviceResources.getScreenHeight()){
			Log.d(TAG, "Long Side: " + DeviceResources.getScreenWidth());
			DeviceResources.longSide = DeviceResources.getScreenWidth();
		}
		else{
			Log.d(TAG, "Long Side: " + DeviceResources.getScreenHeight());
			DeviceResources.longSide =  DeviceResources.getScreenHeight();
		}
	}
}
