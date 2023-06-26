package com.tinocs.greenfoot.text;

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Make subclasses of Button to make quick simple buttons.
 * <ul>
 * 		<li>Override the onClick() method to define what happens when this button is clicked.</li>
 * 		<li>Override the onPressed() method to do something when the mouse is pressed (but not yet released).</li>
 * 		<li>Override the onMouseMoved() to do something when the mouse moves on this button.</li>
 * </ul>
 * 
 * @author Ted McLeod 
 * @version 6/25/2023
 */
public abstract class Button extends Text {

	/** Create a Button with the given text label
	 * @param txt the text label for the button
	 */
    public Button(String txt) {
        super(txt);
    }
    
    /** Every frame a button calls onMouseMoved() if the mouse moved, calls onPressed() if the
     * mouse moved on this button and calls onClick() if the mouse was clicked (mouse button released)
     * on this button.
     */
    public void act() {
    	if (Greenfoot.mouseMoved(this)) onMouseMoved();
    	if (Greenfoot.mousePressed(this)) onPressed();
    	if (Greenfoot.mouseClicked(this)) onClick();
    }
    
    /** Called when the mouse is moved on this button. */
    public void onMouseMoved() {}
    
    /** Called when the mouse is pressed on this button. */
    public void onPressed() {}
    
    /** Called when the mouse is clicked (released) on this button. */
    public abstract void onClick();
}
