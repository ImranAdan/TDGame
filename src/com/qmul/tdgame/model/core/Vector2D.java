/**
 * 
 */
package com.qmul.tdgame.model.core;

import android.util.Log;

/**
 * Class used to encapsulate the vector.
 * 
 * @author Imran
 * 
 */
public class Vector2D {

	public static final int SCALAR_SPEED = 1;

	private static final String TAG = Vector2D.class.getSimpleName();

	public float x;
	public float y;

	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public final float getX() {
		return x;
	}

	public final float getY() {
		return y;
	}

	public final void setX(float x) {
		this.x = x;
	}

	public final void setY(float y) {
		this.y = y;
	}

	public static Vector2D subtract(Vector2D t, Vector2D v) {
		return new Vector2D(t.x - v.x, t.y - v.y);
	}

	public static void normalise(Vector2D v) {
		float l = length(v);
		float x = v.getX() / l;
		float y = v.getY() / l;
		v.setX(x);
		v.setY(y);
	}

	public static float length(Vector2D v) {
		return (float) Math.sqrt(v.x * v.x + v.y * v.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

	private static float dot(Vector2D position, Vector2D vector) {
		return (position.x * vector.x) + (position.y * vector.y);
	}

	public static Vector2D angleAsVector(float angle) {
		angle = (float) Math.toRadians(angle);
		float x = 0;
		float y = 0;
		x = (float) Math.cos(angle);
		y = (float) Math.sin(angle);
		return new Vector2D(x, y);
	}

	public static Vector2D multiplyByScalar(Vector2D v, float scalar) {
		return new Vector2D(v.x * scalar, v.y * scalar);
	}

}