import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PlayerStartLoc here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PlayerStartLoc extends Actor {
    
    public PlayerStartLoc(int playerNum) {
        setImage(new GreenfootImage("" + playerNum, 20, Color.RED, Color.GREEN, null));
    }
    
}
