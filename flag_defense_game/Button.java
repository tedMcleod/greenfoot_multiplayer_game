import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Button here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Button extends Text {

    public Button(String txt) {
        super(txt);
    }
    
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            onClick();
        }
    }
    
    public abstract void onClick();
}
