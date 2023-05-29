import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Rectangle here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Rectangle extends Actor {
    
    public Rectangle(int w, int h) {
        setImage(new GreenfootImage(w, h));
    }
    
    public void setColor(Color color) {
        getImage().setColor(color);
        getImage().fill();
    }
    
    public void setDimensions(int w, int h) {
        getImage().scale(w, h);
    }
   
}
