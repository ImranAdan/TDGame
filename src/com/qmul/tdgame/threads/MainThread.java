/**
 * 
 */
package com.qmul.tdgame.threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.qmul.tdgame.view.DrawingPanel;

/**
 * Main Game Thread that represents the game loop. On each thread cycle, the
 * state of the drawing panel is updated and redrawn.
 * 
 * @author Imran
 * 
 */
public class MainThread extends Thread {

	public static final String TAG = MainThread.class.getSimpleName();

	private final static int MAX_FPS = 50;
	private final static int MAX_FRAME_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	private final SurfaceHolder SURFACE_HOLDER;
	private final DrawingPanel GAMEPANEL;
	private boolean paused;
	private boolean running;


	private boolean fastFoward;

	/**
	 * Create a new thread for updating and rendering the game state.
	 * 
	 * @param surfaceHolder
	 *            The Surface Holder object that this thread is running on.
	 * @param gamePanel
	 *            The Drawing panel this thread to run on.
	 */
	public MainThread(SurfaceHolder surfaceHolder, DrawingPanel gamePanel) {
		super();
		this.SURFACE_HOLDER = surfaceHolder;
		this.GAMEPANEL = gamePanel;
	}

	/**
	 * @return The surface Holder.
	 */
	public SurfaceHolder getSURFACE_HOLDER() {
		return SURFACE_HOLDER;
	}

	/**
	 * @return The drawing panel.
	 */
	public DrawingPanel getGAMEPANEL() {
		return GAMEPANEL;
	}

	/**
	 * @return Boolean indicating if this thread is running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Set the running state of this Thread.
	 * 
	 * @param running
	 *            boolean that indicates if this thread is running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	
	/**
	 * @return the fastFoward
	 */
	public final boolean isFastFoward() {
		return fastFoward;
	}

	/**
	 * @param fastFoward the fastFoward to set
	 */
	public final void setFastFoward(boolean fastFoward) {
		this.fastFoward = fastFoward;
	}

	@Override
	public void run() {
		Canvas canvas;
		long beginTime, timeDiff;
		int sleepTime, framesSkipped;
		sleepTime = 0;
		while (running) {
			canvas = null;
			try {
				canvas = SURFACE_HOLDER.lockCanvas();
				synchronized (SURFACE_HOLDER) {
					if (paused) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
							beginTime = System.currentTimeMillis();
							framesSkipped = 0;
							if(fastFoward){
								for(int i = 0; i<2; i++)
									GAMEPANEL.update();
							}else{
								GAMEPANEL.update();
							}
							GAMEPANEL.render(canvas);
							timeDiff = System.currentTimeMillis() - beginTime;
							sleepTime = (int) (FRAME_PERIOD - timeDiff);

							if (sleepTime > 0) {
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {
								}
							}

							while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
								GAMEPANEL.update();
								sleepTime += FRAME_PERIOD;
								framesSkipped++;
							}
						


					}
				}
			} finally {
				if (canvas != null) {
					SURFACE_HOLDER.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}