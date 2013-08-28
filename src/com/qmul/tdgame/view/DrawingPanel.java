/**
 * 
 */
package com.qmul.tdgame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.qmul.tdgame.R;
import com.qmul.tdgame.controller.GameController;
import com.qmul.tdgame.model.core.Tile;
import com.qmul.tdgame.threads.MainThread;
import com.qmul.tdgame.util.GameResources;



/**
 * Drawing Panel is the only view that is always  updated by the game loop.
 * @author Imran Adan
 *
 */
public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback{

	public static final String TAG = DrawingPanel.class.getSimpleName();

	private final GameController controller = GameController.getController();
	public MainThread mainThread;

	/**
	 * Create a new drawing panel.
	 * @param context The context.
	 */
	public DrawingPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		GameResources.setDrawingPanelWidth(this.getWidth());
		GameResources.setDrawingPanelHeight(this.getHeight());
    	mainThread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	/**
	 * Create a new drawing panel.
	 * @param context
	 * @param attrs
	 */
	public DrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		GameResources.setDrawingPanelWidth(this.getWidth());
		GameResources.setDrawingPanelHeight(this.getHeight());
    	mainThread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	/**
	 * Create a new drawing panel.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public DrawingPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
		GameResources.setDrawingPanelWidth(this.getWidth());
		GameResources.setDrawingPanelHeight(this.getHeight());
    	mainThread = new MainThread(getHolder(), this);
		setFocusable(true);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) { // Surface will not change during program execution, therefore no need to implement.
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		GameResources.setDrawingPanelWidth(this.getWidth());
		GameResources.setDrawingPanelHeight(this.getHeight());
        if (mainThread.getState() == Thread.State.TERMINATED) {
        	mainThread = new MainThread(getHolder(), this);
        	mainThread.setRunning(true);
        	mainThread.start();
        }
        else {
    		controller.initialDrawingPanelSetUp(this.getWidth(), this.getHeight());
        	mainThread.setRunning(true);
        	mainThread.start();
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mainThread.setRunning(false);
		boolean retry = true;
		while (retry) {
			try {
				mainThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Update all models in the game.
	 */
	public void update() {
		if(mainThread.isRunning())
			controller.updateModels();
	}
	
	/**
	 * Render all models using the canavs.
	 * @param canvas The can to be drawn to.
	 */
	public void render(Canvas canvas){
			if(mainThread.isRunning())
				controller.renderGame(canvas);
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(outOfBounds(event.getX(), event.getY()) || event.getX() <= Tile.TILE_WIDTH){ // dirty hack
			GameActivity.getInstance().updateInfoImage(R.drawable.msg_builderror);
			GameController.getController().playSFX(SoundPlayer.SFX.SOUND_ERROR);
			return false;
		}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch (controller.getGame().getGameState()) {
			case BUILD:
				controller.spawn(event.getX(), event.getY());
				break;
			case SELL:
				controller.sellAt(event.getX(), event.getY());
				break;
			case UPGRADE:
				controller.upgradeAt(event.getX(), event.getY());
			}
			return true;
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			switch(controller.getGame().getGameState()){
			case BUILD:
				controller.clearDesiredItem();
				GameActivity.weaponPlacementActive = false;
				break;
			case SELL:
				GameActivity.sellingActive = false;
				break;
			case UPGRADE:
				GameActivity.upgradingActive = false;
				break;
			}

			controller.getGame().setGameState(GameController.WAVE);
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * If a a specified x and y values are out of bounds.
	 * @param x  value
	 * @param y value
	 * @return If out of bounds
	 */
	private boolean outOfBounds(float x, float y) {
		return GameController.getController().getGame().getMap().getGame2DGrid().outOfBounds(x, y);
	}
}
