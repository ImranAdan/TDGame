package com.qmul.tdgame.model.enums;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Paint bank that contains configured paint object. For Types of situations.
 * 
 * @author Imran
 * 
 */
public enum PaintBank {

	/*
	 * Character Key: In Building Mode: a - Available Tile for placement. u -
	 * Unavailable for placement. e - Unavailable for placement area is
	 * restricted for enemy spawning g - Draw black borders around the tiles r -
	 * High light effect Radius of an item object.
	 * 
	 * In Wave Mode: n - Normal paint for in game mode, i.e. clear
	 */

	/**
	 * Available Tile for placement
	 */
	AVAILABLE('a'),

	/**
	 * Unavailable for placement
	 */
	UNAVAILABLE('u'),

	/**
	 * Unavailable for placement area is restricted for enemy spawning
	 */
	ENEMY_SPWANS('e'),

	/**
	 * Draw black borders around the tiles
	 */
	BORDER('g'),

	/**
	 * High light Effect Radius of an item object.
	 */
	EFFECT_RADIUS('r'),

	
	GOOD_HEALTH('G'),
	
	MIDDLE_HEALTH('O'),
	
	LOW_HEALTH('R'),
	
	/**
	 * Normal paint for in game mode, i.e. clear
	 */
	NORMAL('n'),

	SELL('s');

	private char c;
	private Paint paint;

	private PaintBank(char c) {
		paint = new Paint();
		this.c = c;
		switch (c) {

		case 'a':
			paint.setARGB(128, 51, 153, 102); // Light Green with 128 Alpha
			paint.setStyle(Paint.Style.FILL);
			break;
		case 'u':
			paint.setColor(Color.RED);
			paint.setAlpha(128);
			paint.setStyle(Paint.Style.FILL);
			break;
		case 'e':
			paint.setColor(Color.BLUE);
			paint.setAlpha(128);
			paint.setStyle(Paint.Style.FILL);
			break;
		case 'n':
			paint.setColor(Color.BLACK);
			paint.setAlpha(0);
			paint.setStyle(Paint.Style.STROKE);
			break;
		case 'g':
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			break;
		case 'r':
			paint.setARGB(128, 255, 179, 0); // light orange kind of colour.
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			break;
		case 's':
			paint.setColor(Color.MAGENTA);
			paint.setAlpha(0);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2f);
			break;

		case 'G':
			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.FILL);
			break;

		case 'O':
			paint.setARGB(128, 255, 179, 0	);
			paint.setStyle(Paint.Style.FILL);
			break;

		case 'R':
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);
			break;
		}

	}

	public Paint getPaint() {
		return paint;
	}

	public String toString() {
		switch (this.c) {
		case 'a':
			return "GREEN";
		case 'u':
			return "RED";
		case 'n':
			return "CLEAR";
		case 'g':
			return "GRID";
		case 'e':
			return "BLUE";
		case 'r':
			return "Orange";
		}
		return "EMPTY";
	}
}