import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class LocalActor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class LocalActor extends GameActor {
    
    public LocalActor(String id) {
        super(id);
    }
    
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (getWorldOfType(GameWorld.class).getClient() != null) {
            getWorldOfType(GameWorld.class).getClient().sendMessage("MOVE " + getId() + " " + getX() + " " + getY());
        }
    }
    
    @Override
    public void setRotation(int rotation) {
        super.setRotation(rotation);
        if (getWorldOfType(GameWorld.class).getClient() != null) {
            getWorldOfType(GameWorld.class).getClient().sendMessage("ROT " + getId() + " " + getRotation());
        }
    }
}
