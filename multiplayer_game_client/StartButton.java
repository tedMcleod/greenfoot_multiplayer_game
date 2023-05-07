import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javafx.application.Platform;
/**
 * Write a description of class StartButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartButton extends Actor {
    
    public StartButton() {
        GreenfootImage label = new GreenfootImage("START", 20, Color.RED, null);
        getImage().drawImage(label, getImage().getWidth() / 2 - label.getWidth() / 2, getImage().getHeight() / 2 - label.getHeight() / 2);
    }
    
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            GameWorld gw = new MyGameWorld();
            gw.setClient(new MyGameClient("localhost", 1234));
            gw.getClient().start();
            Greenfoot.setWorld(gw);
        }
    }
}
