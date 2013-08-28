package com.qmul.tdgame.view;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.qmul.tdgame.R;
import com.qmul.tdgame.util.GameResources;

/**
 * This class is a sound bank which contains an object to play main themes and this class
 * also contains a nested static class that is used to play all the sound effects.
 * @author Imran
 *
 */
public class SoundPlayer {


	private static final String TAG = SoundPlayer.class.getSimpleName();
	public static final int BUTTON_PRESSED = R.raw.btnpressed;
	
	private static SoundPlayer soundManager;
	private MediaPlayer mediaPlayer;
	private int currentTrack;
	
	private boolean managerStarted;
	private boolean paused;
	private OnCompletionListener completionListener;
	
	/**
	 * Create a new Sound player instance.
	 */
	private SoundPlayer(){
		currentTrack = R.raw.prep;
		mediaPlayer = MediaPlayer.create(GameResources.currentContext, currentTrack);
		managerStarted = true;
		completionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.start();
			}
		};	
		mediaPlayer.setOnCompletionListener(completionListener);
	}
	
	/**
	 * Play the main theme. 
	 */
	public void play(){
		paused = false;
		mediaPlayer.start();
	}
	
	/**
	 * Pause the main theme.
	 */
	public void pause(){
		paused = true;
		mediaPlayer.pause();
	}
	
	/**
	 * stop playback of the main theme.
	 */
	public void stop(){
		paused = true;
		mediaPlayer.release();
	}
	

	/**
	 * restart the sound player.
	 */
	public void restart() {
		if(!managerStarted){
			managerStarted = true;
			mediaPlayer = MediaPlayer.create(GameResources.currentContext, currentTrack);
		}
		play();
	}
	
	/**
	 * destroy the sound player.
	 */
	public void destroy(){
		if(mediaPlayer.isPlaying())
			mediaPlayer.stop();
		paused = true;
		managerStarted = false;
		mediaPlayer.reset();
	}
	
	/**
	 * Set the tune the media player should play
	 * @param id The id of the desired tune.
	 */
	public void setTune(int id){
		currentTrack = id;
		mediaPlayer = MediaPlayer.create(GameResources.currentContext, id);
		mediaPlayer.setOnCompletionListener(completionListener);
	}
	
	/**
	 * Get a instance of the sound player.
	 * @return
	 */
	public static SoundPlayer getInstance(){
		if(soundManager == null)
			soundManager = new SoundPlayer();
		return soundManager;
	}

	/**
	 * @return the paused
	 */
	public final boolean isPaused() {
		return paused;
	}


	/**
	 * @param paused the paused to set
	 */
	public final void setPaused(boolean paused) {
		this.paused = paused;
	}

	
	/**
	 * @return the currentTrack
	 */
	public final int getCurrentTrack() {
		return currentTrack;
	}


	/**
	 * @param currentTrack the currentTrack to set
	 */
	public final void setCurrentTrack(int currentTrack) {
		this.currentTrack = currentTrack;
	}


	/**
	 * @return the managerStarted
	 */
	public final boolean isManagerStarted() {
		return managerStarted;
	}


	/**
	 * @param managerStarted the managerStarted to set
	 */
	public final void setManagerStarted(boolean managerStarted) {
		this.managerStarted = managerStarted;
	}
	
	
	/**
	 * @return the mediaPlayer
	 */
	public final MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}


	/**
	 * @param mediaPlayer the mediaPlayer to set
	 */
	public final void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}


	/**
	 * Nested static class the gives values to sounds once they are loaded.
	 * @author Imran Adan
	 *
	 */
	public static class SFX{
		public static int SOUND_INCOMING = 0;
		public static int SOUND_SOLD = 0;
		public static int SOUND_UPGRADE = 0;
		
		public static int SOUND_SHOT = 0;
		public static int SOUND_ROCKET = 0;
		
		public static int SOUND_ERROR = 0;	
	}
}
