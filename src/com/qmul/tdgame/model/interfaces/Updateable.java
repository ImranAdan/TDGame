/**
 * 
 */
package com.qmul.tdgame.model.interfaces;

/**
 * Updateable interface represents any object who's internal state needs constant
 * updating. This interface needs to be implemented by most entities in the game loop.
 * @author imran
 *
 */
public interface Updateable {
	
	/**
	 * Update the current state of this non-moving object.
	 * Internal setting such as level, health etc.
	 */
	public <T> void update(T... properties);
}
