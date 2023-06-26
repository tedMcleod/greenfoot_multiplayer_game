import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import com.tinocs.greenfoot.text.*;

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TitleWorld extends World
{

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public TitleWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1);
        
        Text title = new Text();
        title.setText("Pong");
        title.setSize(60);
        title.setForeground(Color.RED);
        title.setOutline(Color.YELLOW);
        addObject(title, getWidth() / 2, getHeight() / 4);
        
        
        
        
    }
}
