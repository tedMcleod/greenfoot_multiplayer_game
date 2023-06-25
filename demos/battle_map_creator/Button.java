import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Button here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Button extends Text {
    
    private ClickHandler clickHandler;
    
    public Button() {
        super();
    }
    
    public Button(String txt) {
        super(txt);
    }
    
    public void act() {
        MouseInfo mi = Greenfoot.getMouseInfo();
        if (clickHandler != null && mi != null && mi.getButton() == 1 && mi.getActor() == this && mi.getClickCount() > 0) {
            clickHandler.onClick(this);
        }
    }
    
    public void setOnClick(ClickHandler handler) {
        clickHandler = handler;
    }
}
