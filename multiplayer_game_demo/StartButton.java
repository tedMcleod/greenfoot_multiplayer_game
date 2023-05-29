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
            TitleWorld tw = (TitleWorld)getWorld();
            TitleWorld.lastAddress = tw.getAddress();
            TitleWorld.lastPort = "" + tw.getPort();
            GameWorld gw = new MyGameWorld();
            gw.setClient(new DemoClient(tw.getAddress(), tw.getPort()));
            gw.getClient().start();
            Greenfoot.setWorld(gw);
        }
    }
}
